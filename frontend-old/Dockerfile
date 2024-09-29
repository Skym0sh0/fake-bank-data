FROM arm32v7/node:13.0.1-alpine
#FROM node:13.0.1-alpine

RUN npm install -g http-server@0.11.1

WORKDIR /app

COPY dist/ ./

EXPOSE 8080

CMD [ "http-server", "." ]
