# Busy Screen

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

This application consists of backend implemented as a [Node RED flow](busy-screen.flow) and a frontend implemented with Kotlin JS.

Consequently, you'll need a Node RED installation and a webserver to provide access to the frontend.

### Install on a Raspberry Pi

The Raspberry Pi is the recommended way to run Busy Screen which has been successfully tested on a:

- Raspberry Pi 1 Model B+
- Raspberry Pi Zero / W / WH
- Raspberry Pi 4

The installation consists of the following steps:

1) Create an image using `./busy-screen.build.sh`  
   ![Creation of the image](docs/installation.png)
2) Flash your memory card with the just created image
3) Boot your Raspberry Pi with the memory card **and a working internet connection**
    - Some steps such as the Docker installation must be done on the actual device.
    - Therefore, an internet connection is needed on first boot.
    - The easiest way to provide internet access is using an ethernet cable.

After 1-2 automatic reboots the loading screen shows up.

![loading screen](docs/loading-small.gif)  
**Loading screen**

### Install Manually / Locally

The manual installation consists of the following steps:

1) [Install Node RED](https://nodered.org/docs/getting-started/)
2) Import [busy-screen.flow](busy-screen.flow) to Node RED
3) Build the frontend with `./gradlew build -x test`
4) Set up an HTTP server to publish the [just built frontend](build/distributions), e.g. using `npx http-server -c -p 80`
5) open the published frontend  
   (automatically opened if you use the `npx` command above)
6) change the `location` query parameter in the URL to the one of your Node RED installation

## Responsive Design

![loading screen on large device](docs/loading-large.gif)  
**Loading screen on large device**

![responsive previews with busy state](docs/responsive-busy.jpg)  
**Responsive previews with busy state**

![responsive previews with done state](docs/responsive-done.jpg)  
**Responsive previews with done state**

## Debugging

![loading screen with error](docs/loading-error.gif)  
**Loading screen with error message**

## Architecture

- ImgCstmzr
    - bkahlert/libguestfs
        - pi / copy-out
    - Koodies

## Useful Commands

`yarn upgrade-interactive` to upgrade dependencies

## To-Dos

- [ ] Use https://github.com/nostalgic-css/NES.css directly (embed, no CDN)

## Copyright

Nintendo owns the copyright to Mario, Samus, the heart container, the coin and the controller. Please comply with the Nintendo guidelines and laws of the
applicable jurisdiction.
