# Gradle配置阿里镜像

* **对单个项目生效，在项目中的build.gradle修改内容**

  ```groovy
  buildscript {
      repositories {
          maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
          maven{ url 'http://maven.aliyun.com/nexus/content/repositories/jcenter'}
      }
      dependencies {
          classpath 'com.android.tools.build:gradle:2.2.3'
  
  // NOTE: Do not place your application dependencies here; they belong
  // in the individual module build.gradle files
      }
  }
  
  allprojects {
      repositories {
          maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
          maven{ url 'http://maven.aliyun.com/nexus/content/repositories/jcenter'}
      }
  }
  ```

  

* **对所有项目起作用**

> 在USER_HOME/.gradle/下创建init.gradle文件

```groovy
allprojects{
    repositories {
        def ALIYUN_REPOSITORY_URL = 'http://maven.aliyun.com/nexus/content/groups/public'
        def ALIYUN_JCENTER_URL = 'http://maven.aliyun.com/nexus/content/repositories/jcenter'
        all { ArtifactRepository repo ->
            if(repo instanceof MavenArtifactRepository){
                def url = repo.url.toString()
                if (url.startsWith('https://repo1.maven.org/maven2')) {
                    project.logger.lifecycle "Repository ${repo.url} replaced by $ALIYUN_REPOSITORY_URL."
                    remove repo
                }
                if (url.startsWith('https://jcenter.bintray.com/')) {
                    project.logger.lifecycle "Repository ${repo.url} replaced by $ALIYUN_JCENTER_URL."
                    remove repo
                }
            }
        }
        maven {
            url ALIYUN_REPOSITORY_URL
            url ALIYUN_JCENTER_URL
        }
    }
}
```

