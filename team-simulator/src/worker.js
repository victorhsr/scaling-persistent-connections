const { createWorkerConnection } = require("./connectionFactory");
const { v4: uuid } = require("uuid");

class Worker {
  #isActive = false;
  #lastTrackingData = undefined;
  #trackingUpdateDelay = 0;
  #connection = undefined;
  #isConnectionClosed = false;
  #workInterval = undefined;

  constructor(team, strategy, trackingUpdateDelay) {
    this.id = uuid();
    this.team = team;
    this.strategy = strategy;
    this.#trackingUpdateDelay = trackingUpdateDelay;
    this.#connection = createWorkerConnection(this.strategy);
  }

  startWorking() {
    if (this.#isConnectionClosed) {
      throw Error(
        `The connection for this worker (${this.id}) on team ${this.team} is already closed`
      );
    }

    this.#isActive = true;
    this.#workInterval = setInterval(() => {
      if (!this.#isActive) {
        clearInterval(this.#workInterval);
        return;
      }
      this.#sendNewTrackingData();
    }, this.#trackingUpdateDelay);
  }

  stopWorking() {
    this.#isActive = false;
    this.#connection.end();
  }

  #sendNewTrackingData() {
    const trackingDataReference =
      this.#lastTrackingData || new TrackingData(undefined, this.id, this.team);

    const trackingDataToSend = trackingDataReference.nextRandom();
    this.#connection.write(JSON.stringify(trackingDataToSend));
    this.#lastTrackingData = trackingDataToSend;
  }
}

class TrackingData {
  constructor(id, workerId, team, location) {
    this.id = id || uuid();
    this.workerId = workerId;
    this.team = team;
    this.location = location || new Location();
    this.timeStamp = resolveCurrentDate();
  }

  nextRandom() {
    return new TrackingData(
      this.id,
      this.workerId,
      this.team,
      this.location.nextRandom()
    );
  }
}

class Location {
  constructor(latitude, longitude) {
    this.latitude = (latitude || getRandomInt(-90, 90)).toString();
    this.longitude = (longitude || getRandomInt(-180, 180)).toString();
  }

  nextRandom() {
    const locationVariance = 0.1;
    const newLatitude =
      Math.random() < 0.5
        ? this.latitude
        : varyValue(Number(this.latitude), locationVariance);

    const newLongitude =
      Math.random() < 0.5
        ? this.longitude
        : varyValue(Number(this.longitude), locationVariance);

    return new Location(newLatitude, newLongitude);
  }
}

function resolveCurrentDate() {
  const date = new Date();

  const fillValWithZero = (value) => value.toString().padStart(2, "0");

  const day = fillValWithZero(date.getDate());
  const month = fillValWithZero(date.getMonth());
  const year = date.getFullYear();
  const hours = fillValWithZero(date.getHours());
  const minutes = fillValWithZero(date.getMinutes());
  const seconds = fillValWithZero(date.getSeconds());

  return `${day}/${month}/${year} ${hours}:${minutes}:${seconds}`;
}

function getRandomInt(min, max) {
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function varyValue(value, variance) {
  const operation = Math.random() < 0.5 ? (a, b) => a + b : (c, d) => c - d;

  const varianceValue = value * variance;
  let normalizedVarianceValue;

  if (Math.abs(varianceValue) > 1) {
    normalizedVarianceValue = 1 * Math.sign(varianceValue);
  } else {
    normalizedVarianceValue = varianceValue;
  }

  return operation(value, normalizedVarianceValue).toFixed(8);
}

module.exports = Worker;
