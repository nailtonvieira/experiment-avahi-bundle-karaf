
# Simulador de Things com Zeroconf

Primeiro, é preciso entender o que é o mDNS e qual é o seu propósito. O melhor material prático que encontrei foi esse: [https://developer.apple.com/library/archive/documentation/Cocoa/Conceptual/NetServices/Introduction.html](https://developer.apple.com/library/archive/documentation/Cocoa/Conceptual/NetServices/Introduction.html). Ele dá exemplos e uma ótima explicação.

## Fazendo testes com Avahi usando o Raspberry PI

Abaixo estão os passos para publicar serviços via RaspberryPI e encontrar esses serviços:

 - [ ] Executar **sudo service avahi-daemon status** Caso o comando não exista, instalar o Avahi no Raspberry PI: `sudo apt-get install db5.1-util libgcrypt11-dev avahi-daemon libavahi-client-dev libdb5.3-dev` e `sudo update-rc.d avahi-daemon defaults`
 - [ ] Acessar a pasta **/etc/avahi/services** do Raspberry PI
 - [ ] Adicione o XML abaixo (como um novo arquivo) para criar um novo service
 
>     <?xml version="1.0" standalone='no'?>
>     <!DOCTYPE service-group SYSTEM "avahi-service.dtd">
>     <service-group>
>      <name replace-wildcards="yes">nome_do_servico</name>
>       <service>
>        <type>_mqtt._tcp</type>
>        <port>1883</port>
>        <txt-record>minha_variavel=arduino_wiser_001</txt-record>
>        <txt-record>outra_variavel=driver_sensor_temperatura</txt-record>
>       </service>
>     </service-group>

 
 
 - [ ] Recarregar o Avahi service com **sudo systemctl reload avahi-daemon** ou reiniciar com **sudo systemctl restart avahi-daemon**
 - [ ]  Instalar o aplicativo Service Browser no Android: [https://play.google.com/store/apps/details?id=com.druk.servicebrowser&hl=en](https://play.google.com/store/apps/details?id=com.druk.servicebrowser&hl=en)
 - [ ] Entrar na mesma rede (wifi ou Ethernet) que o Raspberry PI está
 - [ ] Verificar no aplicativo se o service **nome_do_servico** está presente no aplicativo
 - [ ] Você também pode publicar manualmente pela CLI do Avahi ex: **avahi-publish -s nome_do_servico _mqtt._tcp 1883**. Aqui está um ótimo tutorial sobre os comandos do CLI do Avahi: [https://www.win.tue.nl/~johanl/educ/IoT-Course/mDNS-SD%20Tutorial.pdf](https://www.win.tue.nl/~johanl/educ/IoT-Course/mDNS-SD%20Tutorial.pdf)
 - [ ] Você pode usar o comando ping para encontrar o raspberry na rede: `ping raspberrypi.local` a partir de qualquer outro dispositivo na mesma rede. Isso é possível porque o Avahi já configura um nome com .local na rede, assim que entra na rede.

Mais informações e exemplos sobre o arquivo XML do Avahi aqui: [https://kodi.wiki/view/Avahi_Zeroconf#What_about_it.3F](https://kodi.wiki/view/Avahi_Zeroconf#What_about_it.3F)

## Instalar o Avahi client no Ubuntu

    sudo apt-get install avahi-common
    sudo apt-get install libavahi-common-dev
    sudo apt-get install libavahi-glib-dev
    sudo apt-get install libavahi-client-dev

## Baixar o arquivo e descompactar

https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/blob/master/avahi_JNI/avahi_JNI.tar

    Copiar o avahi4j.jar para o diretório /usr/share/java 
    Copiar libavahi4j.so para o diretório /usr/lib 
    Dar permissão de read outros usuários no libavahi4j.so

Na imagem abaixo é mostrado um caso de erro, quando o libavahi4j.so ou o avahi4j.jar não estiverem com as permissões ou diretório correto.

![Caso a lib não esteja disponível](https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/blob/master/img/1_caso_de_erro.png)

Baixar o projeto e dar build no projeto:

[https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/thing-simulator/pswot-avahi-things-simulator](https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/thing-simulator/pswot-avahi-things-simulator)

> Esse é o simulador de things, ele publica e descobre dispositivos na
> rede. Você pode adaptar o código para publicar dispositivos simulados
> personalizados e/ou escutar dispositivos indefinidamente (ler seção de
> Zeroconf).

Para testar e ver se os dispositivos simulados estão publicando e o Avahi funcionando, basta executar o things Simulator como aplicação java normal, pela própria IDE. Você deve ver no console os dispositivos que estão publicando e sendo descobertos, e logo imprimindo seus dados do TXTRecord.

1. Executa o Main do ThingsSimulator

![Executa o Main do ThingsSimulator](https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/blob/master/img/2_executa_thing_simulator.png)

3. Executa o Main DetectorSimulator

![Executa o DetectorSimulator](https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/blob/master/img/3_executa_detector.png)

4. Aperta enter na tela no console do ThingsSimulator

![Aperta enter na tela do ThingsSimulator](https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/blob/master/img/4_aperta_enter_no_thing_simulator.png)

5. Aperta enter na tela no console do DetectorSimulator

![Aperta enter na tela no console do DetectorSimulator](https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/blob/master/img/5_aperta_enter_no_detectot_simulator.png)

Assim, na tela do DetectorSimulator você verá as mensagens publicadas pelo ThingsSimulator.

A diferença entre o DetectorSimulator e o Detecter (projeto com avahi que será implantado no servicemix) é que o Detecter executa dentro do ServiceMix, como um bundle.

## Instalar ZeroConf e MQTT no dispositivo fisico

 1. Implantar código no Arduino com Ethernet:

[https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/mDNSArduino](https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/mDNSArduino)

> Neste link tem mais dois links. O primeiro com a IDE (já com todas as
> dependências) e segundo com o código do Arduino atualizado (MQTT + Zeroconf).

 2. Conectar o Arduino ao roteador com cabo Ethernet

## Instalar o Detecter no ServiceMIX

3. Baixar o projeto [https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/pswot-gateway-avahi-detecter](https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/pswot-gateway-avahi-detecter) implantar esse bundle no servicemix.

4. Em [https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/bundles](https://github.com/nailtonvieira/experiment-avahi-bundle-karaf/tree/master/bundles) estão os Bundles que devem ser colocados no servicemix.

5. Para o próximo passo você já deve ter colocado os bundles no servicemix, o código no Arduino e o broker para rodar.

6. Com isso feito, quando você reconectar o Arduino ao cabo ethernet no terminal do servicemix deve aparecer o log da descoberta do Arduino. Expandindo o Detector você pode fazer o que quiser com os dados do TXT Record, uma vez que eles estavam disponíveis no código java.

7. A imagens seguintes mostram o Detecter implantado como bundle dentro do servicemix descobrindo os dados do Arduino. Você pode acompanhar belo Wireshark as trocas de mensagens mDNS pela rede.

8.  Quando você colocar o Detecter no servicemix vai aparecer a mensagem abaixo:

<p align="center">
  <img src="https://github.com/nailtonvieira/cloudsemanticwot/blob/master/others/README-Elements/wireshark1.png"/>
</p>

10. Quando você conectar o Arduino na rede ethernet vai aparecer a mensagem abaixo:
 
<p align="center">
  <img src="https://github.com/nailtonvieira/cloudsemanticwot/blob/master/others/README-Elements/wireshark2.png"/>
</p>

11. Quando você remover o Arduino da rede ethernet vai aparecer a mensagem abaixo:
 
<p align="center">
  <img src="https://github.com/nailtonvieira/cloudsemanticwot/blob/master/others/README-Elements/wireshark3.png"/>
</p>

12. **Todos os eventos do fluxo do mDNS são capturados no Detecter. A partir deles você pode implementar novas features.**
