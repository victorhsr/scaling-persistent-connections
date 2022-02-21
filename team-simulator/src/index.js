const Worker = require("./worker");

const kafkaWorkers = [...Array(80)].map((_, index) => {
  const worker = new Worker("team_two", "kafka", "550");
  worker.startWorking();
  return worker;
});

const redisWorkers = [...Array(95)].map((_, index) => {
  const worker = new Worker("team_one", "redis", "550");
  worker.startWorking();
  return worker;
});

function exitHandler(cause) {
  console.log("Releasing connections, cause:", cause);

  const releaseConnection = (worker) => worker.stopWorking();

  kafkaWorkers.forEach(releaseConnection);
  redisWorkers.forEach(releaseConnection);
}

process.on("exit", exitHandler.bind(null, "exit"));
process.on("SIGINT", exitHandler.bind(null, "SIGINT"));
process.on("SIGUSR1", exitHandler.bind(null, "SIGUSR1"));
process.on("SIGUSR2", exitHandler.bind(null, "SIGUSR2"));
process.on("uncaughtException", exitHandler.bind(null, "uncaughtException"));
