FROM node:22-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM node:22-alpine AS runtime

WORKDIR /app

ENV NODE_ENV=production
ENV PORT=4200

COPY package*.json ./
RUN npm ci --omit=dev

COPY --from=build /app/dist/frontend ./dist/frontend

EXPOSE 4200

CMD ["node", "dist/frontend/server/server.mjs"]
