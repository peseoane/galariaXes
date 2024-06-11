FROM alpine:latest

RUN apk update && apk add openjdk21 maven nmap

ENV TZ=Europe/Madrid