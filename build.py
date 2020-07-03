"""
本脚本用于构建整个应用，包括后段部分，前端部分以及eidolon核心库。

构建步骤如下：
1. 构建基于 React 的 UI 部分。使用命令 npm run build
2. 将生成的静态资源目录 build 放入 DeepStudio/src/main/resources 目录下
3. 将该 build 目录改名为 static.
4. 构建后端部分。使用命令 mvnw clean package

"""

import os
import shutil
import subprocess

# 构建完保存的目录
distPath = "dist"

# 如果存在dist目录，则先删除
if os.path.exists(distPath) == True:
    shutil.rmtree(distPath)
    print("Old dist removed.")

# 创建构建完的目录
os.mkdir(distPath)
print("make dist dir.")

# 切换到react前端目录
uiDir = os.path.join(".", "UI", "deep-studio-ui")
os.chdir(uiDir)
print("change directionary to {}".format(uiDir))
# 执行构建指令
child = subprocess.Popen("npm run build",shell=True)
child.wait()
# 回到根目录
os.chdir("../../")
print("React UI built.")

# 将构件好的前端代码转移到 DeepStudio/src/main/resources
# 获取静态目录位置
staticDir = os.path.join(".", "DeepStudio", "src",
                         "main", "resources", "static")
# 如果该存在，则删除该目录
if os.path.exists(staticDir) == True:
    shutil.rmtree(staticDir)
    print("Old static dir removed.")
shutil.copytree(os.path.join(uiDir, "build"), staticDir)
print("Static UI dir moved to dist.")

# 构建后端
os.chdir(os.path.join(".", "DeepStudio"))
# 使用mvnw构建后端
child = subprocess.Popen("mvnw clean package", shell=True)
child.wait()
print("Spring boot backend built.")
# 回到根目录
os.chdir("../")

# 将该构建的jar移至dist目录
shutil.copyfile(os.path.join(".", "DeepStudio", "target",
                             "DeepStudio-1.0-SNAPSHOT.jar"), os.path.join(".", "dist", "DeepStudio.jar"))
print("Jar file moved to dist")

#移动eidolon
shutil.copytree(os.path.join(".","Eidolon-Tensorflow", "eidolon"), os.path.join(".","dist","eidolon"))
print("Eidolon moved to dist")

shutil.copytree(os.path.join(".", "DeepStudio","drawable"), os.path.join(".","dist","drawable"))
print("drawable moved to dist")

with open("./dist/start.bat", "w") as f:
    f.writelines(["javaw -jar DeepStudio.jar\n","exit"])

print("Build Success.")
