# Pegasus

## Introduction

Pegasus is a message broker, used to build asynchronous messaging system

### Publish-Subcscribe

- With pub-sub model, many senders can send messages to an entity on server (topic in JMS).
- There can be many *subscriptions* on a topic, a subscription is similar to a consumer. Each subscription receives a copy of message from topic. This differs from message queue pattern where each message is only consumed once by a single consumer
- Subscriptions can optionanally be durable which means they retain a copy of each message sent to a topic until the subscriber consumes theme - even if the server crashes or restarts. Non-durable subscriptions only last a period of lifetime.

### Deliveries

- A key feature of most messaging system is reliable messaging. With reliability, the server gives a gurantee that the message will be deliveried once and only once to each consumer of a queue (subscription).

### Transactions

- Messaging system typically support the sending and acknowledgement of multiple messages in a single local transaction. In this case, we are using Java mapping of XA: JTA

### Durability

- Messages are either durable or non-durable, durable message will be persisted in permanent storage, while non-durable message will be in memory.

### Messaging API

#### JMS & Jakarta Messaging

- JMS is a Java API that encapsulates both message queue and pub-sub pattern. It is a lowest common denominator specification - it was created to encapsulate common funtionality of already existing messaging system.
- JMS is only available to clients running Java.
- JMS does not define a standard wire format - it only defines a programmatic API so clients and servers from different vendors cannot directly interoperate since each will use the vendor's own internal wire protocol.

### Bridges and routing

- Some messaging system allows isolated clusters or single node to be bridged together, typically over unreliable connections like WAN, Internet.
- A bridge normally consumes from a queue on one server and forwards messages to another queue on a different server. Bridges cope with unreliable connections, automatically reconnecting when the connections becomes available again.