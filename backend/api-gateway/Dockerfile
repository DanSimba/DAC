FROM node:22-alpine

WORKDIR /gateway

COPY package*.json .

RUN npm install

COPY . .

EXPOSE 8080

CMD ["node","index.js"]
