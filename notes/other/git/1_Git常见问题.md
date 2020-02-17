
### Push was rejected, and update failed with error.
 * 解决方案如下: 
```shell
git pull origin master --allow-unrelated-histories
```
* 我们可以在git pull 后面添加一个 "--allow-unrelated-histories" 把两个分支进行强行合并。