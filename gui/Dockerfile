FROM node:lts-alpine

RUN npm install -g http-server

WORKDIR /app

COPY package*.json ./

RUN npm install

ADD regular-income-gui-*.tgz .

RUN mv ./package/* . \
    && rm -rf package

RUN npm run build

EXPOSE 8080

CMD [ "http-server", "dist" ]
