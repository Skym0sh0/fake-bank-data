FROM arm32v7/node:13.0.1-alpine

RUN npm install -g http-server

WORKDIR /app

COPY package*.json ./

RUN npm install

ADD regular-income-gui-*.tgz .

RUN mv -f ./package/* ./package/.env* . \
    && rm -rf package \
    && npm run build

EXPOSE 8080

CMD [ "http-server", "dist" ]
