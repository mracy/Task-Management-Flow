$ErrorActionPreference = "SilentlyContinue"
Set-Location "C:\Internal\Pesonal project\Task Management Platform"
git commit --amend --no-edit 2>&1 | Out-File git_final_push.txt
git push --force origin main 2>&1 | Out-File git_final_push.txt -Append
"EXIT=$LASTEXITCODE" | Out-File git_final_push.txt -Append
