Simulador de Things com Zeroconf

1) Instalar o Avahi client no Ubuntu

1.1) Executar
sudo apt-get install avahi-common
sudo apt-get install libavahi-common-dev
sudo apt-get install libavahi-glib-dev
sudo apt-get install libavahi-client-dev

1.2) 
- Baixar o arquivo e descompactar https://github.com/nailtonvieira/cloudsemanticwot/blob/master/experiment/avahi_JNI.tar
- Copiar o avahi4j.jar para /usr/share/java e o libavahi4j.so para /usr/lib/jni

2) Baixar o projeto https://github.com/nailtonvieira/cloudsemanticwot/tree/master/experiment/thing-simulator/pswot-avahi-things-simulator e instalar dependências com o maven.

3) Esse é o simulador de things, ele publica e descobre dispositivos na rede. Você pode adaptar o código para publicar dispositivos simulados personalizados e/ou escutar dispositivos indefinidamente (ler seção de Zeroconf).

3.1) Para testar e ver se os dispositivos simulados estão publicando e o Avahi funcionando, basta executar o things Simulator como aplicação java normal, pela própria IDE. Você deve ver no console os dispositivos se publicando e sendo descobertos, e logo imprimindo seus dados do TXTRecord.

Instalar ZeroConf e MQTT no dispositivo fisico, e o Detecter no ServiceMIX

4) Implantar código no Arduino com Ethernet https://github.com/nailtonvieira/cloudsemanticwot/tree/master/experiment/mDNSArduino - Neste link tem mais dois links com a IDE (já com todas as dependências) e o código do Arduino atualizado (MQTT + Zeroconf)

4.1) Conectar dispositivo ao roteador com cabo Ethernet

5) Baixar container Docker do Mosquitto MQTT

6) Executar container Docker Mosquitto MQTT

7) Instalar Client Mosquito MQTT no Ubuntu (ler e aprender a usar o MQTT)

8) Executar o cliente mqtt no terminal

9) Baixar o projeto https://github.com/nailtonvieira/cloudsemanticwot/tree/master/pswot-gateway-projects/pswot-gateway-avahi-detecte/e instalar dependências com o maven, este bundle é colocado no servicemix.

9.1) Em https://github.com/nailtonvieira/cloudsemanticwot/tree/master/experiment/bundles estão os Bundles para descoberta que devem ser colocados no servicemix (incluso o do pswot-gateway-avahi-detecter).

11) Aqui você já deve ter colocado os bundles no servicemix, o código no Arduino e o broker para rodar.

11.2) Com isso feito, quando você reconectar o Arduino ao cabo ethernet no terminal do servicemix deve aparecer o log da descoberta do Arduino. Expandindo o detectar você pode fazer o que quiser com os dados do TXT Record, uma vez que eles estavam disponíveis no código java.


