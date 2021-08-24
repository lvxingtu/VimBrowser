FROM gitpod/workspace-full-vnc

RUN sudo apt-get update \
    && curl -s "https://get.sdkman.io" | bash \
    && sdk install java 16.0.2.fx-librca \
    && apt-get install -y openjfx libopenjfx-java matchbox \
    && apt-get clean && rm -rf /var/cache/apt/* && rm -rf /var/lib/apt/lists/* && rm -rf /tmp/*

