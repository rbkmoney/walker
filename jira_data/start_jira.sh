#!/usr/bin/env bash
# to run clean Jira
# docker run -t -p 2990:2990 translucent/atlassian-plugin-sdk:latest atlas-run-standalone --product jira
# to run pre configured Jira
docker run -t -p 2990:2990 walker/jira:0.1 atlas-run-standalone --jvmargs "-Xmx2048m" --product jira