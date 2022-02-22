import React, { useEffect, useRef, useState } from "react";
import styled from "@emotion/styled";
import { default as OlMap } from "ol/Map";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import OSM from "ol/source/OSM";
import Geometry from "ol/geom/Geometry";
import { Feature } from "ol";
import Point from "ol/geom/Point";
import Style from "ol/style/Style";
import Circle from "ol/style/Circle";
import {
  count,
  distinct,
  Observable,
  Subscription,
  switchMap,
  windowTime,
} from "rxjs";
import { fromLonLat } from "ol/proj";
import Fill from "ol/style/Fill";
import TeamDetails from "./teamDetails";
import { TrackingStrategy } from "../../service/trackingService";

interface TeamMapProps {
  team: string;
  strategy: TrackingStrategy;
  dataStream: Observable<MapTrackingData>;
}

interface MapTrackingData {
  id: string;
  latitude: number;
  longitude: number;
}

const TeamMap: React.FunctionComponent<TeamMapProps> = (props) => {
  const { dataStream, strategy, team } = props;

  const [vectorSource, setVectorSource] = useState<VectorSource<Geometry>>();
  const [subscriptions, setSubscriptions] = useState<Subscription[]>([]);
  const [totalWorkers, setTotalWorkers] = useState(0);
  const mapRef = useRef<HTMLDivElement>(null);
  const markerMap = new Map<string, Feature<Point>>();
  const windowTimeDuration = 500;

  useEffect(() => {
    initMap();
  }, []);

  useEffect(() => {
    markerMap.clear();
    vectorSource?.clear();
    startListeningForUpdates();
    return () => {
      setSubscriptions((currentSubscriptions) => {
        currentSubscriptions.forEach((subscription) =>
          subscription.unsubscribe()
        );
        return [];
      });
    };
  }, [team, dataStream, strategy]);

  function initMap() {
    const olMap = new OlMap({
      target: mapRef.current!,
      layers: [
        new TileLayer({
          source: new OSM(),
        }),
      ],
      view: new View({
        projection: "EPSG:3857",
        center: [0, 0],
        zoom: 0,
      }),
      controls: [],
    });

    var vectorSource = new VectorSource({});
    var markersLayer = new VectorLayer({
      source: vectorSource,
    });

    olMap.addLayer(markersLayer);

    setVectorSource(vectorSource);
  }

  function startListeningForUpdates() {
    const dataWindow = dataStream.pipe(windowTime(windowTimeDuration));

    const mapTrackingDataWindowCountSubsc = dataWindow
      .pipe(
        switchMap((x) =>
          x.pipe(
            distinct((mapTrackingData: MapTrackingData) => mapTrackingData.id),
            count()
          )
        )
      )
      .subscribe((totalDataReceived) => {
        setTotalWorkers(totalDataReceived);
        if (totalDataReceived === 0) {
          markerMap.clear();
          setVectorSource((vectorSource) => {
            vectorSource?.clear();
            return vectorSource;
          });
        }
      });

    const mapTrackingDataSubsc = dataWindow
      .pipe(
        switchMap((x) =>
          x.pipe(
            distinct((mapTrackingData: MapTrackingData) => mapTrackingData.id)
          )
        )
      )
      .subscribe((mapTrackingData: MapTrackingData) => {
        const markerJustArrived = createMarker(mapTrackingData);

        if (!markerMap.has(mapTrackingData.id)) {
          markerMap.set(mapTrackingData.id, markerJustArrived);
          setVectorSource((vectorSource) => {
            vectorSource?.addFeature(markerJustArrived);
            return vectorSource;
          });
          return;
        }

        const existingMarker = markerMap.get(mapTrackingData.id)!;
        existingMarker.setGeometry(markerJustArrived.getGeometry());
      });

    setSubscriptions((currentSubscriptions) => {
      return [
        ...currentSubscriptions,
        mapTrackingDataSubsc,
        mapTrackingDataWindowCountSubsc,
      ];
    });
  }

  return (
    <Container>
      <MapContainer ref={mapRef} />
      <TeamDetails
        strategy={strategy}
        totalWorkers={totalWorkers}
        team={team}
        refreshTime={windowTimeDuration}
      />
    </Container>
  );
};

function createMarker(mapTrackingData: MapTrackingData) {
  const iconStyle = new Style({
    image: new Circle({
      radius: 5,
      fill: new Fill({
        color: getRandomColor(),
      }),
    }),
  });
  const marker = new Feature({
    geometry: new Point(
      fromLonLat([mapTrackingData.longitude, mapTrackingData.latitude])
    ),
  });
  marker.setStyle(iconStyle);
  marker.setId(mapTrackingData.id);

  return marker;
}

/**
 * from https://stackoverflow.com/questions/1484506/random-color-generator
 */
function getRandomColor() {
  var letters = "0123456789ABCDEF";
  var color = "#";
  for (var i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
}

const Container = styled.div({
  height: "100%",
  width: "100%",
  display: "flex",
});

const MapContainer = styled.div({
  height: "100%",
  width: "100%",
  filter: "invert(100%)",
});

export type { MapTrackingData };
export default TeamMap;
