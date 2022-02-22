# build phase
FROM node:latest as builder

WORKDIR /usr/src/app

COPY package*.json ./
RUN npm install

COPY src src
COPY public public
COPY tsconfig.json tsconfig.json

ARG REACT_APP_REDIS_TRACKING_LOCATION
ARG REACT_APP_KAFKA_TRACKING_LOCATION

RUN npm run build

# actual container
FROM nginx:latest
COPY --from=builder /usr/src/app/build /usr/share/nginx/html

EXPOSE 80
ENTRYPOINT [ "nginx", "-g", "daemon off;" ]