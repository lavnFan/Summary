<center>
# git-repo
</center>

## git
### 1、git的工作空间
![git的工作空间](http://7xrhn4.com1.z0.glb.clouddn.com/git%E5%B1%82%E6%AC%A1.png)

* Woking directory(工作目录)   
* Staging area(暂存区)。执行git add 命令后，修改会被暂存staging area。
* local repository(本地库)
* remote repository(远程库)

### 2、git中文件的几种状态
　　![](http://7xrhn4.com1.z0.glb.clouddn.com/Git%E4%B8%AD%E6%96%87%E4%BB%B6%E7%9A%84%E5%87%A0%E7%A7%8D%E7%8A%B6%E6%80%81.png)

* untracked：在woking directory中新创建了一个文件，这个文件的状态就是untracked。
* tracked：Git库上已存在的文件，就是tracked，对于此类文件又可以做如下区分
	* unmodifed：从远程库下载了此文件，但是还没有修改。
	* modifed：修改了，还没有执行git add命令。
	* staged：执行了git add命令后，此文件的修改已经被缓存到staged area
	* committed：执行了git commit命令，此文件的修改被保存到本地库。

### 3、git操作流程
#### git add
* 运行 git branch 命令可以确认当前分支名   
尝试修改一个文件后保存退出。运行git status命令可以查看当前的仓库状态。   
* git status中的提示非常有用。
Changes not staged for commit:  提示当前的修改仍在woking directory，not staged
(use "git add <file>..." to update what will be committed)   提示用git add命令可以进行代码提交。
(use "git checkout -- <file>..." to discard changes in working directory)  提示如果清除掉此次修改所使用的命令。   
* git add -A：添加所有的修改到staged area
* git add envsetup.sh 只添加envsetup.sh文件的修改到staged area
* git add . 添加untracked file和文件的修改到缓存区，注意会忽略掉文件的删除操作，比如你删除了文件Android.mk，执行这个命令并不会把把删除操作提交到staged area
* git add -u 添加tracked file的修改到缓存区，注意会忽略掉untracked file，也就是新增的文件。比如编译过程中产生了很多新的中间文件，使用此命令可以忽略掉这些中间文件。

#### git commit 
git commit -m "BugID:XXX:Description"  常用的一种格式。注意commit message格式错误可能会导致入库失败。
还有一种方式是直接执行git commit命令，再打开的新窗口中输入commit message。注意git默认的编辑器是nano，习惯用vim的同学可以修改Git默认编辑器

#### git push
git push remote_repository loacl_branch:remote_branch     

**常用的格式如下** 

* git push orgin master:refs/for/master  提交本地的master分支的修改到远程库origin的refs/for/master 分支
* git push yunos HEAD:refs/for/kphone_6572_k268 提交本地库当前分支的修改到远程库的refs/for/kphone_6572_k268

#### git log
常用的几种组合是   

* git log 查看log
* git log -p   查看每次提交的详细修改
*　git log --pretty=oneline 每个commit只显示一行，这个命令在处理代码冲突conflict时非常有用
* git log --name-status 适用于查看外部库的修改记录

#### git status
里面的提示信息非常有用，忘记下一步该执行什么命令时就运行下git status

git 命令时带上目录，比如git status ./build 只查看build目录下的代码状态，对于外部库可以大幅减少执行时间


#### git reset
这个命令在回退代码，或者重新提交时非常有用。常用的几种组合有：

* git reset --mixed：此为默认方式，同不带参数的git reset，只保留woking directory的修改，回退staging area 和 local repository的修改
* git reset --soft：回退到某个版本，只回退local repository。如果还要提交，直接commit即可
* git reset --hard：彻底回退到某个版本，本地的源码也会变为上一个版本的内容，比如 git reset --hard HEAD
*其他回退:：当修改了woking directory的文件a.txt，还没有执行git add命令，这时候要丢弃这些修改，可使用命令
git checkout -- a.txt

#### 分支操作
* git branch 查看分支
* git branch new_branch 创建新分支
* git branch -a 查看所有分支
* git branch -r 查看远程分支
* git checkout existed_branch
* git checkout -b new_branch

#### 其他git命令
* git pull 拉取远程库上的最新代码更新合并到本地当前分支。
* fetch   只是将远程的数据拉到本地仓库，并不自动合并到当前分支，需要手动合并。
* 简单理解   git pull = git fetch + git merge
* git init   初始化一个git库，--bare参数用来创建server端的基本库。
* git tag  打标签，相当于特定的commit id起一个别名。
* git remote  查看远程库的名称

## repo
### 1、repo init
格式为 repo init -u repository_url -b branch_name   

#### 2、同步代码 repo sync
同步代码到本地，如果本地代码中有修改或未push的新提交，可能会repo sync失败。   
repo sync -c 只下载当前分支的代码，减少repo sync时间,但之后要先fetch才能cherry pick其他分支的修改。-c, --current-branch  fetch only current branch from server

### 3、创建本地分支
repo sync后运行repo branch命令的结果为   
$ repo branch    
   (no branches)    
即没有本地分支，因此要先创建本地分支，分支名自定义。    
hangtao.yht@ubuntu:~/kphone/kphone_clone3$ repo start sn_test --all    
操作后再执行repo branch可以看到    
hangtao.yht@ubuntu:~/kphone/kphone_clone3$ repo branch   
*  sn_test                   | in all projects   

如果不创建本地分支，在no branch下操作存在丢失代码修改的风险。   

### 4、修改代码
进入各个目录修改代码，下面列举一些容易出问题的地方：   

#### 根目录下的文件
如果修改的代码是根目录下的mk_aliphone.sh/mm.sh/imgout等文件，修改后提交是无效的，因为这些文件在根目录下的只是一个副本，要到源目录下去修改才可以。   
查找源目录的方法如下，以mk_aliphone.sh为例，打开文件.repo/manifest.xml，查找关键词mk_aliphone.sh，可以找到如下信息：   
这里的src即为源文件，要修改这个文件才有效。

#### 编译后的Git库处理
在修改完代码后，我们可能会进行编译，编译完后会产生一些中间文件，这时在进行提交时要注意不要把中间文件给提交了。推荐的操作顺序是：   
先修改，然后提交到本地库，然后编译验证。验证通过后用repo upload命令进行提交。如果验证失败可以用git clean命令清理掉中间文件，然后用git reset命令回退最近的commit，再次修改后进行git commit。   
git clean -df 清除掉working directory中的untracked file and directories。如果要清理整个Android代码，可以使用repo forall -c "git clean -df"    
用git add -u 命令也会忽略掉untracked files   
如果修改的文件只有一两个，可以直接用命令 git add <file1> <file2>

### 5、代码提交到本地库
#### case 1：修改了3个Git库的代码，但这些修改使用同样的commit message，如BugID相同，就可以使用如下命令   

* repo forall -c "git add xxx"   
* repo forall -c "git commit -m'BugID:000:Description'"

但执行会比较耗时，因为要遍历所有Git库进行同样的操作。   

####case 2：修改了多个Git库，而且commit message也各不相同，这时候就要在各个Git库进行git commit操作。

### 6、代码提交到Gerrit
#### 方法一、git push命令。   
git push HEAD:refs/for/kphone_6572_k268   
但这种方法需要知道远程分支的参数，容易出错，建议使用repo upload
#### 方法二、repo upload
使用repo upload可能会出现提交失败的情况，常见的错误原因有两种，一是在没有使用repo start命令创建本地分支，另外一种是执行repo upload后打开的窗口中要去掉提交的project前的注释#号。 


## repo库和git库
* repo库是由若干个git库组成，使用repo下载代码相当于下载了所有这些Git库的同一个分支，然后把下载的代码放到不同的目录。
* git中的哪些版本记录数据放哪里了？在.repo目录，这个目录的大小可能是整套代码的1/2。在代码目录下也有一些.git目录，但是里面的大部分文件和目录都是软链接，链接到.repo目录。
* repo命令的时间开销比较大，因为相当于对repo管理下的每个Git库都遍历执行同样的操作。如果只修改一个Git库，可以只下载单个Git库的此产品分支，修改后用git命令提交，节约下载和执行repo命令的时间。
* 整套代码根目录下的文件放在哪个Git库，如mm.sh mk_aliphone.sh，这些文件是放在单个Git库的，然后repo下载的时候拷贝到根目录，如果要修改这些文件，必须到对应的Git目录下修改和提交，在根目录下提交无效。

