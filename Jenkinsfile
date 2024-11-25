pipeline {
    agent any
    environment {
        image="535324349245.dkr.ecr.ap-northeast-1.amazonaws.com/bb-server:"
    }
    stages {
        stage('获取CommitID') {
            steps {
                script {
                    env.imageTag = sh (script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                }
            }
        }
        stage('set buildDescription') {
            steps {
                // 自定义设置构建历史显示的名称和描述信息 
                   // 不同的部署方式设置构建历史显示的名称和描述信息方式不一样，根据自己的部署方式自行百度找到设置方法
                script {
                    //设置buildName
                    wrap([$class: 'BuildUser']) {
                            buildDescription "<span style='padding-right: 10px;font-weight:bold;font-size:13px;color: #1E90FF;'>发布人:${BUILD_USER}     分支: ${TAG} </span>"
                    }
                }
            }
        }
        stage('编译') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('构建镜像及上传') {
            steps {
                sh """
                    docker build -t ${env.image}${imageTag}-${env.BUILD_ID} .
                    aws ecr get-login-password --region ap-northeast-1 | docker login --username AWS --password-stdin 535324349245.dkr.ecr.ap-northeast-1.amazonaws.com
                    docker push ${env.image}${imageTag}-${env.BUILD_ID}
                """
            }
        }
        stage('发布') {
            steps {
                sh """
                    kubectl --kubeconfig /home/jenkins/.kube/ex-pro -nex-pro set image deployment bb-server bb-server=${env.image}${imageTag}-${env.BUILD_ID}
                    kubectl --kubeconfig /home/jenkins/.kube/ex-pro -nex-pro set image deployment bb-server-sub bb-server-sub=${env.image}${imageTag}-${env.BUILD_ID}
                """
            }
        }
    }
}
