import { Observable, Subject } from "rxjs";

enum TrackingStrategy {
  REDIS = "REDIS",
  KAFKA = "KAFKA",
}

abstract class TrackingDataProvider {
  private readonly subject: Subject<TrackingData>;
  private connection: EventSource | undefined;

  readonly team: string;
  readonly strategy: TrackingStrategy;
  readonly stream: Observable<TrackingData>;

  constructor(team: string, strategy: TrackingStrategy) {
    this.team = team;
    this.strategy = strategy;
    this.subject = new Subject<TrackingData>();
    this.stream = this.subject;
  }

  init() {
    if (this.connection) return;

    console.log("initializing ", this.team, this.strategy, this.resolvePath());

    this.connection = new EventSource(this.resolvePath());

    const that = this;
    this.connection.onmessage = function (event) {
      const trackingData = JSON.parse(event.data);
      that.subject.next(trackingData);
    };
  }

  dispose() {
    this.connection?.close();
    this.connection = undefined;
  }

  abstract resolvePath(): string;
}

class RedisTrackingDataProvider extends TrackingDataProvider {
  constructor(team: string) {
    super(team, TrackingStrategy.REDIS);
  }

  resolvePath(): string {
    return process.env.REACT_APP_REDIS_TRACKING_LOCATION + `/${this.team}`;
  }
}

class KafkaTrackingDataProvider extends TrackingDataProvider {
  constructor(team: string) {
    super(team, TrackingStrategy.KAFKA);
  }

  resolvePath(): string {
    return process.env.REACT_APP_KAFKA_TRACKING_LOCATION + `/${this.team}`;
  }
}

class TrackingManager {
  private providers = new Map<String, TrackingDataProvider>();

  getTrackingStream(
    team: string,
    strategy: TrackingStrategy
  ): Observable<TrackingData> {
    const providerKey = this.resolveTrackinProviderKey(team, strategy);
    let provider = this.providers.get(providerKey);

    if (provider == null) {
      provider = this.createNewProvider(team, strategy);
      this.providers.set(providerKey, provider);
    }

    provider.init();
    return provider.stream;
  }

  disposeTracking(team: string, strategy: TrackingStrategy) {
    const providerKey = this.resolveTrackinProviderKey(team, strategy);

    let provider = this.providers.get(providerKey);
    provider?.dispose();
  }

  private resolveTrackinProviderKey(team: string, strategy: string) {
    return `${strategy}_${team}`;
  }

  private createNewProvider(
    team: string,
    strategy: TrackingStrategy
  ): TrackingDataProvider {
    switch (strategy) {
      case TrackingStrategy.REDIS:
        return new RedisTrackingDataProvider(team);
      case TrackingStrategy.KAFKA:
        return new KafkaTrackingDataProvider(team);
      default:
        throw new Error(`Strategy ${strategy} was not mapped`);
    }
  }
}

interface TrackingData {
  workerId: string;
  readonly id: String;
  readonly team: String;
  readonly location: { latitude: string; longitude: string };
  readonly timeStamp: string;
}

const trackingManager = new TrackingManager();
export default trackingManager;
export { TrackingStrategy };
export type { TrackingData };
