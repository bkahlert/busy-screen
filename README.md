![Two Kaomoji shouting "Busy?!"](docs/banner.png)

# Busy Screen [![Build Status](https://img.shields.io/github/actions/workflow/status/bkahlert/busy-screen/build.yml?label=Build&logo=github&logoColor=fff)](https://github.com/bkahlert/busy-screen/actions/workflows/build.yml) [![License](https://img.shields.io/github/license/bkahlert/busy-screen?color=29ABE2&label=License&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA1OTAgNTkwIiAgeG1sbnM6dj0iaHR0cHM6Ly92ZWN0YS5pby9uYW5vIj48cGF0aCBkPSJNMzI4LjcgMzk1LjhjNDAuMy0xNSA2MS40LTQzLjggNjEuNC05My40UzM0OC4zIDIwOSAyOTYgMjA4LjljLTU1LjEtLjEtOTYuOCA0My42LTk2LjEgOTMuNXMyNC40IDgzIDYyLjQgOTQuOUwxOTUgNTYzQzEwNC44IDUzOS43IDEzLjIgNDMzLjMgMTMuMiAzMDIuNCAxMy4yIDE0Ny4zIDEzNy44IDIxLjUgMjk0IDIxLjVzMjgyLjggMTI1LjcgMjgyLjggMjgwLjhjMCAxMzMtOTAuOCAyMzcuOS0xODIuOSAyNjEuMWwtNjUuMi0xNjcuNnoiIGZpbGw9IiNmZmYiIHN0cm9rZT0iI2ZmZiIgc3Ryb2tlLXdpZHRoPSIxOS4yMTIiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48L3N2Zz4%3D)](https://github.com/bkahlert/busy-screen/blob/master/LICENSE) [![Buy Me A Unicorn](https://img.shields.io/static/v1?label=&message=Buy%20Me%20A%20Unicorn&color=c21f73&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA3MiA3MiI%2BPHBhdGggZmlsbD0iI0ZGRiIgZD0iTTIzLjc1NCAxMi4zNjJsMS42NjcgNy4xNjctNS4zMzMgNS4zMzMtOC4zMzQgMTQuMzMzIDEgNC42NjcgMi4xNjcgMS4zMzMgNC0uMTY3IDMuNS0zLjMzMyA2LjgzMy0xLjgzM3MxLjMzNCAxLjUgMi4xNjcgMyAzLjY2NyA0LjE2NyAzLjY2NyA0LjE2N2wuNSA2LTEuODMzIDYuMTY2LTIgMi44MzNzMjIgOS41IDMzLjE2Ni03bC0uNS02LTEuODMzLTUtMy4zMzMtNS4xNjYtMS0xLjUtLjE2Ny01LjE2Ny0yLjgzMy01LjMzMy01LTMtMi42NjctNC41LTUuMTY3LTQuMTY3LTYuNS0xLjUtNS42NjYgMS00LjE2Ny0yLjE2Ny0yLjMzNC0uMTY2eiIvPjxwYXRoIGZpbGw9IiNFQTVBNDciIGQ9Ik01MC42NzEgMjMuMTU1bDUuMjA4IDQuMDk1czUuNTY0IDguMjE4LS4zMjUgMTcuODJjLTcuMDUgMTEuNDkyIDAgMCAwIDAtMS42MTkgMy40NzUtMi4zMTUgNi43NDItMS43MzkgOS43MjJsLTUuMzEtNC40MTdWMzQuMjkybDIuMTY2LTExLjEzN3pNMjUuODk4IDE5LjI3MWwtMTUuMTEzLTcuMjUgNS4xNjYgNi4xMTkgNS4yMjQgNS44NTUgNC43MjMtNC43MjQiLz48cGF0aCBmaWxsPSIjOTJEM0Y1IiBkPSJNMjkuNzM3IDEzLjYzMWwxMC43NjcuMTM2czkuMjM4IDQuMDY2IDEwLjUzNiAxMS44MTZsLjY4NyA4Ljk1N2MtMi42MzMgNi41MzktMy4wNTYgMTQuMTI3IDIuMDg5IDIwLjgzNCAwIDAtNy4xNDUgMS4zMjEtOS44OTUtNy4xMUw0Mi4zMzggNDMuNWwuMzI1LTYuMDM0IDEuNDE3LTUuNjQzLS4yODMtNC44OTMtMi4yNzYtNC4zMTItMy41MzItMi44NDEtNS43OTItMi4wOC0yLjQ2LTQuMDY2Ii8%2BPHBhdGggZmlsbD0iIzYxQjJFNCIgZD0iTTU4LjQ1NSAzNi43NXM1LjUyIDYuNDA3IDYuOTk4IDE1LjEyYTguMDIgOC4wMiAwIDAxLS4xMzggMy4yNThjLS40MzEgMS43NTItLjgxNyA0Ljk5OC4xNDYgNy4zODMuNDY5IDEuMTYxLS41NjIgMi4zNjUtMS43ODkgMi4xMTEtMy43MS0uNzY4LTkuMjQzLTMuNjQ3LTEwLjI1Ni04LjA4N2ExLjgyNiAxLjgyNiAwIDAxLS4wNDItLjMyMWwtLjI2Ni01Ljc0NmMtLjAxMy0uMjg2LjA1Mi0uNTcuMTg3LS44MjFsMy42OTItNi44MzZjLjA2Ny0uMTIzLjExNy0uMjU1LjE0OS0uMzkybDEuMzE5LTUuNjY5Ii8%2BPGc%2BPHBhdGggZmlsbD0ibm9uZSIgc3Ryb2tlPSIjMDAwIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiIHN0cm9rZS1taXRlcmxpbWl0PSIxMCIgc3Ryb2tlLXdpZHRoPSIyIiBkPSJNNTguNDU1IDM3Ljc4M0M2MC4yMjMgNDAuMTQ0IDY1IDQ0LjQ2NSA2NC41IDU0LjAyTTMyLjUgNDEuODg1czguNDc4IDYuNzgzIDAgMTguNzY1TTI0LjgwOSAxOS4xMzRMMTAuMjUgMTEuNzVsMTAuOTI1IDEyLjI0NSIvPjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzAwMCIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBzdHJva2UtbGluZWpvaW49InJvdW5kIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHN0cm9rZS13aWR0aD0iMiIgZD0iTTM1LjE5NiAzMC44N2MuNTUgOC4zNTUtOS4zMjIgOS43MDMtMTEuOTU0IDEwLjMzNC0uMzMyLjA4LS42MzIuMjUtLjg3My40OTJsLTIuMjIzIDIuMjIzYy0uMzUuMzUtLjgyNC41NDYtMS4zMTguNTQ2aC0zLjUxMmEyLjc5NSAyLjc5NSAwIDAxLTIuNjUxLTEuOTExbC0uNTMxLTEuNTkzYTIuNzk1IDIuNzk1IDAgMDEuMjU1LTIuMzIybDguNzg2LTE0LjY0NCA0LjcyNC00LjcyNC0yLjExNi02LjkwNXM3LjgwMy0uNjk5IDguNDE0IDUuMzNjMCAwIDE2LjkyOCAyLjQ0MiAxMC41NTMgMTkuMzg0IDAgMC0xLjYyNSA1Ljk0OSAyLjM3NSAxMS4xODRNMzAuOTE3IDE0LjAyUzUzLjE2IDEwIDUwLjg3NSAzMy45OCIvPjxwYXRoIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzAwMCIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBzdHJva2UtbGluZWpvaW49InJvdW5kIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHN0cm9rZS13aWR0aD0iMiIgZD0iTTQ5LjkxOSAyMy4xNTVzMTQuNzY2IDYuNTg3IDUuNDU2IDIyLjIyYzAgMC01LjM3NSA2LjU2My42MjUgMTMuNjA0Ii8%2BPGNpcmNsZSBjeD0iMjQuNDE3IiBjeT0iMjguOTMiIHI9IjIiLz48L2c%2BPC9zdmc%2B)](https://www.buymeacoffee.com/bkahlert)

Turns your Raspberry Pi into a status screen to show your colleagues, family, friends, or whoever might disturb you if you're busy or not.

```shell
PUT http://192.168.168.168:1880/status
Content-Type: application/json; charset=utf-8

{
  "name": "making frontend responsive",
  "task": "TICKET-123",
  "duration": "PT50M",
}
```

**Set status to "making frontend responsive" with an estimation of 50 minutes**

![being busy with state "making frontend responsive" for 23 minutes](docs/busy.gif)  
**Being busy with state "making frontend responsive" for 23 minutes**

![no more being busy](docs/done.gif)  
**No more being busy**

## Installation

This application consists of a backend implemented as a [Node RED flow](kustomize/home/busy-screen/flows.json) and a frontend implemented with Kotlin JS.

Consequently, you'll need a Node RED installation and a webserver to provide access to the frontend.

[Install on a Raspberry Pi](ansible/README.md)

When successfully installed, the loading screen shows up.
![Loading screen on Raspberry Pi](docs/raspberry-loading.jpg)

A few moments later the backend can receive status updates, like this one:

```shell
curl -X PUT --location "http://192.168.168.168:1880/status" \
     -H "Content-Type: application/json; charset=utf-8" \
     -d "{
           \"name\": \"finishing soon\",
           \"duration\": \"PT2M\"
         }"
```

![Busy screen on Raspberry Pi](docs/raspberry-busy.jpg)

The following properties are supported:

```json
{
    "name": "status name that is displayed in the speech bubble",
    "task": "task title used as the headline on the top border",
    "duration": "60000",
    "email": "john.joe@example.com",
    "on": {
        "finish": {
            "method": "post",
            "url": "http://my-talking-robot/say",
            "payload": "finished working"
        }
    }
}
```

The only required field is `name`. All other fields are optional.

The `duration` can be specified in

- number of milliseconds (`60000`ms = 1min) or
- in [ISO8601](https://en.wikipedia.org/wiki/ISO_8601) format (`PT1M` / `PT60S` = 1min)

You can find further examples in [http-client.http](http-client.http).

#### Connectivity

The connectivity options depend on your [Pi Hero configuration](https://github.com/bkahlert/pihero#accessible).

#### Discovery

If you start your device with a connected screen, you see the following information that help you finding your device:

- Your device **name** is written on the left border.
- Your device **IP** is written on the right border.
- Your **username** is written below the avatar.
- **Nearby devices** are listed in a dropdown that opens when you click your username.
    - The device currently connected to is always on top.
    - Using the "Switch" button you can connect to the selected device.

![Discovery Options](docs/discovery.png)

Avahi is installed on your Raspberry Pi with all relevant services advertised in your network. You can use any zeroconf / mDNS / Bonjour client to discover your
device.

![iNet Network Scanner](docs/bonjour.png)

Alternatively you can log in to your router and find out what new devices received a dynamic IP address from it.

### Install Manually / Locally

The manual installation consists of the following steps:

1) [Install Node RED](https://nodered.org/docs/getting-started/)
2) Import [busy-screen.flow](kustomize/home/busy-screen/flows.json) to Node RED
3) Build the frontend with `./gradlew build -x test`
4) Set up an HTTP server to publish the [just built frontend](build/distributions), e.g. using `npx http-server -c -p 80`
5) open the published frontend  
   (automatically opened if you use the `npx` command above)
6) change the `address` query parameter in the URL to the one of your Node RED installation

## Customization

Busy Screen can be customized / extended in three ways:

1) The frontend is located at [src/main/kotlin](src/jsMain/kotlin). You can make any changes you like to it and run the [installation](#installation)
   afterwards.
2) The Node RED flow can be freely changed as you like. In order to customize it, just edit it inside of Node RED. If you followed
   the [installation](#installation) steps above, you already have a running installation.
3) You can customize the way your Raspberry Pi image is created. The image creation is done with the image customization
   tool [Kustomize](https://github.com/bkahlert/kustomize). The actual configuration is stored in [busy-screen.conf](kustomize/busy-screen.conf).

## Responsive Design

![loading screen on small device](docs/loading-small.gif)  
**Loading screen on small device**

![loading screen on large device](docs/loading-large.gif)  
**Loading screen on large device**

![responsive previews with busy state](docs/responsive-busy.jpg)  
**Responsive previews with busy state**

![responsive previews with done state](docs/responsive-done.jpg)  
**Responsive previews with done state**

## Debugging

![loading screen with error](docs/loading-error.gif)  
**Loading screen with error message**

## Known Issues / TODO

- [ ] on Raspberry Pi B+ the Plymouth based loading screen only works  
  after `raspi-config` → Advanced Options → G1 Fake KMS was selected.

- [ ] get network connection to Raspberry Pi booted with dockerpi
    - [ ] check for SSH
    - [ ] check for HTTP
    - [ ] change status and check if page changed

## Copyright

Nintendo owns the copyright to Mario, Samus, the heart container, the coin and the controller. Please comply with the Nintendo guidelines and laws of the
applicable jurisdiction.

South Park characters have been designed with the amazing [SP-Studio](https://www.sp-studio.de/).

## References

- [Adventures with SPI TFT screens for the Raspberry Pi](https://www.willprice.dev/2017/09/16/adventures-with-tft-screens-for-raspberry-pi.html)
- [SPI TFT LCD](https://blog.gc2.at/post/spi-tft-lcd2/)

## Contributing

Want to contribute? Awesome! The most basic way to show your support is to star the project, or to raise issues. You can also support this project by making
a [PayPal donation](https://www.paypal.me/bkahlert) to ensure this journey continues indefinitely!

Thanks again for your support, it is much appreciated! :pray:

## License

MIT. See [LICENSE](LICENSE) for more details.
