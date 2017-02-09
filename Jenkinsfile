#!groovy
build('walker', 'java-maven') {
    def serviceName = "walker"
    def baseImageTag = "f26fcc19d1941ab74f1c72dd8a408be17a769333"

    checkoutRepo()

    // Run mvn and generate docker file
    runStage('Maven package') {
        withCredentials([[$class: 'FileBinding', credentialsId: 'java-maven-settings.xml', variable: 'SETTINGS_XML']]) {
            def mvn_command_arguments = ' --batch-mode --settings  $SETTINGS_XML -P ci ' +
                    " -Ddockerfile.base.service.tag=${baseImageTag} " +
                    " -Dgit.branch=${env.BRANCH_NAME} "
            if (env.BRANCH_NAME == 'master') {
                sh 'mvn deploy' + mvn_command_arguments
            } else {
                sh 'mvn package' + mvn_command_arguments
            }
        }
    }

    def serviceImage;
    getCommitId()
    runStage('Build Service image') {
        serviceImage = docker.build('rbkmoney/' + "${serviceName}" + ':' + '$COMMIT_ID', '-f ./target/Dockerfile ./target')
    }

    sh 'docker images'

    if (env.BRANCH_NAME == 'master') {
        runStage('Push Service image') {
            docker.withRegistry('https://dr.rbkmoney.com/v2/', 'dockerhub-rbkmoneycibot') {
                serviceImage.push();
            }
            sh 'docker images'
            // Push under 'withRegistry' generates 2d record with 'long name' in local docker registry.
            // Untag the long-name
//            sh "docker rmi dr.rbkmoney.com/${imgShortName}"
        }
    }

}