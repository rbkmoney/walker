# Base image is translucent/atlassian-plugin-sdk:latest
# Current image have preconfigured Jira Claims project and fields.
FROM dr.rbkmoney.com/rbkmoney/walker/test-jira:0.1
CMD ["atlas-run-standalone","-Datlassian.upm.on.demand=true","-Datlassian.upm.on.demand=true","-Dallow.google.tracking=false","--jvmargs", "-Xmx2048m","--product" ,"jira"]
EXPOSE 2990
