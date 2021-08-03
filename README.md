# Busy Screen

Busy Screen is an application to display one's current status to colleagues, family, friends or whoever might disturb your focus.

![loading screen on small device](docs/loading-small.gif)  
**Loading screen on small device**

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

## Responsiveness

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
