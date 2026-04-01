def call(String status, String branchName) {
    def icon = ''
    def title = ''

    if (status == 'SUCCESS') {
        icon = '✅'
        title = 'Pipeline PASSED 🎉'
    } else if (status == 'FAILURE') {
        icon = '❌'
        title = 'Pipeline FAILED'
    } else if (status == 'ABORTED') {
        icon = '⚠️'
        title = 'Pipeline ABORTED'
    } else {
        icon = 'ℹ️'
        title = "Pipeline ${status}"
    }

    env.TG_MSG = """
      ${icon} *${title}*
      *Job:* `${env.JOB_NAME}`
      *Build:* [#${env.BUILD_NUMBER}](${env.BUILD_URL})
      *Branch:* `${branchName}`
      *Duration:* ${currentBuild.durationString}
      *Stage Info:* `${env.STAGE_NAME ?: 'Unknown'}`
    """.stripIndent().trim()

    sh '''
      curl -s -X POST https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage \\
        -d chat_id=${TELEGRAM_CHAT_ID} \\
        -d parse_mode=Markdown \\
        --data-urlencode "text=${TG_MSG}"
    '''
}

def notifyResult(String branchName) {
    try {
        def branch = branchName?.trim()
        def status = currentBuild.currentResult ?: 'FAILURE'
        call(status, branch)
    } catch (Exception e) {
        echo "Error: ${e}"
        call('UNSTABLE', branchName)
    }
}

return this
