import { createClient } from 'redis';

export function createRedis({ url, logger = console } = {}) {
  let client;
  let connectionPromise;

  function getClient() {
    if (!client?.isReady) {
      throw new Error('Redis não está conectado.');
    }

    return client;
  }

  async function connect() {
    if (client?.isReady) {
      return client;
    }

    if (connectionPromise) {
      return connectionPromise;
    }

    if (!url) {
      throw new Error('URL do Redis não informada.');
    }

    if (!client) {
      client = createClient({ url });
      client.on('error', error => logger.error('Erro no Redis:', error));
    }

    connectionPromise = client.connect()
      .then(() => client)
      .finally(() => {
        connectionPromise = undefined;
      });

    return connectionPromise;
  }

  async function disconnect() {
    if (!client) {
      return;
    }

    if (client.isOpen) {
      await client.quit();
    }

    client = undefined;
    connectionPromise = undefined;
  }

  return {
    connect,
    disconnect,
    getClient
  };
}
