SHELL := /bin/bash
.DEFAULT_GOAL := help
# Gradle's output directory is called build, so the targets are declared phony.
.PHONY: help gradle npm build browser test-js test-tier0 test-tier1 test-tier2 test test-all vm-device vm-prepare vm display deploy clean release

PLATFORM ?= linux/arm64
TARGET ?=
QEMU_ACCEL ?= hvf
# The Waveshare 3.5-inch panel's size for the VM's virtual display; the display test and make display hardcode the same 480x320.
VM_DISPLAY ?= 480x320
URL ?=
UV := uv run --frozen
GRADLE_ARGS ?= --no-daemon --console=plain
SERVER := packages/busy-screen-server/server

help: ## list targets
	@grep -E '^[a-zA-Z_-]+:.*?## ' $(MAKEFILE_LIST) | awk 'BEGIN{FS=":.*?## "}{printf "  %-14s %s\n", $$1, $$2}'

gradle: ## build the web bundle
	./gradlew $(GRADLE_ARGS) jsBrowserDistribution

npm: ## vendor Node-RED and the flow's nodes from the lock file
	cd $(SERVER) && npm ci --omit=dev --no-audit --no-fund

build: gradle npm ## build the .deb packages into dist/
	@$(UV) python -m pihero_testkit.build

browser: ## download Playwright's WebKit, the kiosk's engine family, for the display test
	@$(UV) playwright install webkit

test-js: ## the display's JS unit tests (Karma, headless Chrome)
	./gradlew $(GRADLE_ARGS) jsBrowserTest

test-tier0: ## unit tests and static checks
	@$(UV) pytest -m tier0

test-tier1: ## install the packages into a systemd container and test
	@$(UV) pytest -m installed --target=podman --platform=$(PLATFORM)

test-tier2: ## boot a VM with the panel's display from the sample device file and run the installed and boot tests
	@$(UV) pytest -m 'installed or boot' --target=vm --qemu-accel=$(QEMU_ACCEL) --display=$(VM_DISPLAY)

test: test-js test-tier0 test-tier1 ## JS unit tests, tiers 0 and 1, what CI runs

test-all: test test-tier2 ## everything, what make release runs

vm-device: ## render the sample device file for the VM into dist/vm-device
	@$(UV) python tests/vm_device.py

vm-prepare: ## build and cache the tier-2 base image under ~/.cache/pihero
	@$(UV) python -m pihero_testkit.prepare

vm: vm-device ## boot the tier-2 VM from the sample device file and keep it running
	@$(UV) python -m pihero_testkit.vm --keep --qemu-accel=$(QEMU_ACCEL) --display=$(VM_DISPLAY) --device=dist/vm-device

display: ## open URL in Playwright's WebKit at the panel's 480x320 (make display URL='http://busy-screen.local/?address=http://busy-screen.local:1880')
	@test -n "$(URL)" || { echo "usage: make display URL='http://host/?address=http://host:1880'"; exit 2; }
	@$(UV) playwright open -b webkit --viewport-size=480,320 "$(URL)"

deploy: build ## install the built packages on TARGET over SSH
	@test -n "$(TARGET)" || { echo "usage: make deploy TARGET=pi@host"; exit 2; }
	@$(UV) python -m pihero_testkit.deploy "$(TARGET)"

clean: ## remove build outputs
	rm -rf dist packages/*/.build build $(SERVER)/node_modules

release: ## run the tiers, then tag VERSION (make release VERSION=1.0.0)
	@test -n "$(VERSION)" || { echo "usage: make release VERSION=X.Y.Z"; exit 2; }
	@git diff --quiet HEAD || { echo "working tree is dirty"; exit 1; }
	@$(MAKE) test-all
	git tag -a "v$(VERSION)" -m "v$(VERSION)"
	@echo "Tagged v$(VERSION). Push with: git push origin v$(VERSION)"
