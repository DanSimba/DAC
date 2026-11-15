import amqp from 'amqplib';

export function createRabbitMQ({
  url,
  connectionAttempts = 10,
  retryDelay = 3000,
  logger = console
} = {}) {
  let connection;
  let channel;
  let connectionPromise;

  function wait(milliseconds) {
    return new Promise(resolve => setTimeout(resolve, milliseconds));
  }

  function getConnection() {
    if (!connection) {
      throw new Error('RabbitMQ não está conectado.');
    }

    return connection;
  }

  function getChannel() {
    if (!channel) {
      throw new Error('Canal do RabbitMQ não está disponível.');
    }

    return channel;
  }

  async function openConnection() {
    let lastError;

    for (let attempt = 1; attempt <= connectionAttempts; attempt += 1) {
      try {
        const newConnection = await amqp.connect(url);
        const newChannel = await newConnection.createChannel();

        connection = newConnection;
        channel = newChannel;

        newConnection.on('error', error => logger.error('Erro na conexão com RabbitMQ:', error));
        newConnection.on('close', () => {
          if (connection === newConnection) {
            connection = undefined;
            channel = undefined;
          }
        });

        newChannel.on('error', error => logger.error('Erro no canal do RabbitMQ:', error));
        newChannel.on('close', () => {
          if (channel === newChannel) {
            channel = undefined;
          }
        });

        return newChannel;
      } catch (error) {
        lastError = error;

        if (attempt < connectionAttempts) {
          logger.warn(`RabbitMQ indisponível. Nova tentativa em ${retryDelay} ms.`);
          await wait(retryDelay);
        }
      }
    }

    throw lastError;
  }

  async function connect() {
    if (channel) {
      return channel;
    }

    if (connectionPromise) {
      return connectionPromise;
    }

    if (!url) {
      throw new Error('URL do RabbitMQ não informada.');
    }

    connectionPromise = openConnection().finally(() => {
      connectionPromise = undefined;
    });

    return connectionPromise;
  }

  async function disconnect() {
    const currentChannel = channel;
    const currentConnection = connection;

    channel = undefined;
    connection = undefined;
    connectionPromise = undefined;

    if (currentChannel) {
      await currentChannel.close();
    }

    if (currentConnection) {
      await currentConnection.close();
    }
  }

  return {
    connect,
    disconnect,
    getConnection,
    getChannel
  };
}
