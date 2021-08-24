FROM gitpod/workspace-full-vnc

RUN curl -s "https://get.sdkman.io" | bash \
    && source "$HOME/.sdkman/bin/sdkman-init.sh" \
    && sdk install java 16.0.1-librca \

