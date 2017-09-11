# git-github

## 配置git

#### 设置username和email

git config --global user.name "your name"

git config --global user.email "your_email@youremail.com"

#### 创建 ssh key

$ ssh-keygen -t rsa -C "your_email@youremail.com"

#### 将ssh写入gitHub

$ cat ~/.ssh/id_rsa.pub

将显示创建好的ssh key复制，再在GitHub-个人头像-settings-SSH and GPG keys中创建新的ssh keys。

#### 验证连接

$ ssh -T git@github.com

若显示：You’ve successfully authenticated, but GitHub does not provide shell access，则表示已经连接成功上GitHub；

若显示：ssh: connect to host github.com port 22: Connection timed out，则可能是网络代理问题，对网络进行代理设置：

$ git config --global https.proxy http://

$ git config --global https.proxy https://

$ git config --global --unset http.proxy

$ git config --global --unset https.proxy
