This repo was made to support an article I'm writing (link will be added soon) about how we can scale up applications that rely on persistent connections, based in a real world scenario I faced some years ago.

## The problem

There is a system where there are teams of workers and each team has a manager who wants to see in 'real time', the location of their workers on a map. The refreshing rate of the worker's position must be as short as possible and the system must be able to handle thousands of workers sending tracking data at the same time. There's also another characteristic, the number of workers online is seasonal, it means, there are periods of time where the number of active workers may spike aggressively or be reduced by half of its usual.

To solve this problem, we are goingo to rely on persistent connections, creating data streams betwen the actors of the system (futher information and full discussion about the problem, you can find on the article). But, what is the issue about scalling up services that rely on persistent connections?

When we have more than one instance of the service that will handle with the persistent connections, one of the intentions is to split the work load between the instances, thus, each instance will keep part of the connections from both, Workers and Managers. If the reading stream, from the Manager users, may not be in the same service that holds the Woker's connection, how will it be able to receive the tracking data from their entire team?

![Representation of the work splitting between the service instances](./arch_figures/persistent_connection_problem.png)

## Solutions

Both solutions presented bellow are pretty similar, both broadcast the tracking data between all of the instances, so it doesn't matter if the Manager's connections is in a different instance than the Worker's ones.

This is a solution that works, to a certain extent, but it still have some tradeoffs and bottlenecks that we have discussed in the article. So, this is like a partial solution, we still are going to improve this architecture to a most robust one in the chapter two of this article, stay tuned!

### Redis

![Architectural proposal for the redis solution](./arch_figures/redis_solution.png)


### Kafka

![Architectural proposal for the redis solution](./arch_figures/kafka_solution.png)