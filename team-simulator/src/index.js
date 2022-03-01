const Worker = require("./worker");

const redisTeam = "team_one";
const kafkaTeam = "team_two";

const kafkaTotalWorkers = process.env.KAFKA_WORKERS || 80
const kafkaUpdateDelay = process.env.KAFKA_UPDATE_DELAY || 550

const redisTotalWorkers = process.env.REDIS_WORKERS || 95
const redisUpdateDelay = process.env.REDIS_UPDATE_DELAY || 550

function createWorker(team, strategy, updateDelay) {
  const worker = new Worker(team, strategy, updateDelay);
  worker.startWorking();
  return worker;
}

console.log("Creating", kafkaTotalWorkers, "worker(s) for kafka, team:", kafkaTeam);
const kafkaWorkers = [...Array(kafkaTotalWorkers)].map((_) => createWorker(kafkaTeam, "kafka", kafkaUpdateDelay));
console.log("Creating", redisTotalWorkers, "worker(s) for redis, team:", redisTeam);
const redisWorkers = [...Array(redisTotalWorkers)].map((_) => createWorker(redisTeam, "redis", redisUpdateDelay));

console.log("Worker(s) created and sending tracking data");

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
