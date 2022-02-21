const http = require("http");

const STRATEGIES = {
  redis: {
    host: process.env.HOST || "localhost",
    port: process.env.PORT || "8080",
    path: process.env.REDIS_PATH || "/tracking/redis/",
  },
  kafka: {
    host: process.env.HOST || "localhost",
    port: process.env.PORT || "8080",
    path: process.env.KAFKA_PATH || "/tracking/kafka/",
  },
};

function createWorkerConnection(strategy) {
  return createConnection(STRATEGIES[strategy]);
}

function createConnection(opts) {
  const optsToUse = {
    ...opts,
    method: "POST",
    headers: {
      "Content-Type": "application/x-ndjson",
    },
  };

  var post_req = http.request(optsToUse, function (res) {
    res.setEncoding("utf8");
    res.on("data", function (chunk) {
      console.log("Response: " + chunk);
    });
  });

  return post_req;
}

module.exports = { createWorkerConnection };
