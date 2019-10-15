# Simulador de Things com Zeroconf

## Instalar o Avahi client no Ubuntu

    sudo apt-get install avahi-common
    sudo apt-get install libavahi-common-dev
    sudo apt-get install libavahi-glib-dev
    sudo apt-get install libavahi-client-dev

## Baixar o arquivo e descompactar

https://github.com/nailtonvieira/cloudsemanticwot/blob/master/experiment/avahi_JNI.tar

    copiar o avahi4j.jar para o diretório /usr/share/java 
    copiar libavahi4j.so para o diretório /usr/lib e dar permissão para outros usuários terem acesso 

Baixar o projeto e dar build no projeto:

[https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/thing-simulator/pswot-avahi-things-simulator](https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/thing-simulator/pswot-avahi-things-simulator)

> Esse é o simulador de things, ele publica e descobre dispositivos na
> rede. Você pode adaptar o código para publicar dispositivos simulados
> personalizados e/ou escutar dispositivos indefinidamente (ler seção de
> Zeroconf).

Para testar e ver se os dispositivos simulados estão publicando e o Avahi funcionando, basta executar o things Simulator como aplicação java normal, pela própria IDE. Você deve ver no console os dispositivos que estão publicando. Para ver o conteudo das mensagens publicadas, você deve executar o DetecterSimulator e assim ver as things sendo descobertas, e logo imprimindo seus dados do TXTRecord.

## Instalar ZeroConf e MQTT no dispositivo fisico

 1. Implantar código no Arduino com Ethernet:

[https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/mDNSArduino](https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/mDNSArduino)

> Neste link tem mais dois links com a IDE (já com todas as
> dependências) e o código do Arduino atualizado (MQTT + Zeroconf).

 2. Conectar dispositivo ao roteador com cabo Ethernet

## Instalar o Detecter no ServiceMIX

3. Baixar o projeto [https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/pswot-gateway-avahi-detecter](https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/pswot-gateway-avahi-detecter) implantar esse bundle no servicemix.

4. Em [https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/bundles](https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/bundles) estão os Bundles para descoberta que devem ser colocados no servicemix.

5. Aqui você já deve ter colocado os bundles no servicemix, o código no Arduino e o broker para rodar.

6. Com isso feito, quando você reconectar o Arduino ao cabo ethernet no terminal do servicemix deve aparecer o log da descoberta do Arduino. Expandindo o detectar você pode fazer o que quiser com os dados do TXT Record, uma vez que eles estavam disponíveis no código java.
