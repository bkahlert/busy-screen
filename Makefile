SHELL := /bin/bash
.DEFAULT_GOAL := help
# Gradle's output directory is called build, so the targets are declared phony.
.PHONY: help gradle npm build browser test-js test-tier0 test-tier1 test-tier2 test test-all vm-device vm-prepare vm display deploy clean release

PLATFORM ?= linux/arm64
TARGET ?=
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

test: test-js test-tier0 test-tier1 ## JS unit tests, tiers 0 and 1

deploy: build ## install the built packages on TARGET over SSH
	@test -n "$(TARGET)" || { echo "usage: make deploy TARGET=pi@host"; exit 2; }
	@$(UV) python -m pihero_testkit.deploy "$(TARGET)"

clean: ## remove build outputs
	rm -rf dist packages/*/.build build $(SERVER)/node_modules

release: ## run the tiers, then tag VERSION (make release VERSION=1.0.0)
	@test -n "$(VERSION)" || { echo "usage: make release VERSION=X.Y.Z"; exit 2; }
	@git diff --quiet HEAD || { echo "working tree is dirty"; exit 1; }
	@$(MAKE) test
	git tag -a "v$(VERSION)" -m "v$(VERSION)"
	@echo "Tagged v$(VERSION). Push with: git push origin v$(VERSION)"
