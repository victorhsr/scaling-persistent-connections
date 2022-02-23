This repo was made to support an article I'm writing (link will be added soon) about how we can scale up applications that rely on persistent connections, based in a real world scenario I faced some years ago.

## The problem

There is a system where there are teams of workers and each team has a manager who wants to see in 'real time', the location of their workers on a map. The refreshing rate of the worker's position must be as short as possible and the system must be able to handle thousands of workers sending tracking data at the same time. There's also another characteristic, the number of workers online is seasonal, it means, there are periods of time where the number of active workers may spike aggressively or be reduced by half of its usual.

## Solutions

For further information, about the problem and how we got these solutions, read the article :P

### Redis

### Kafka