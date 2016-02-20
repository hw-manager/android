# !/bin/bash
# By Nico Alt
# See "LICENSE" for license information

# Download and unzip files
mkdir tmp
cd tmp
wget https://crowdin.com/download/project/hw-manager.zip
unzip hw-manager.zip

# Delete files in app directory
rm -R ../src/main/res/values-ar
rm -R ../src/main/res/values-cs
rm -R ../src/main/res/values-de
rm -R ../src/main/res/values-en
rm -R ../src/main/res/values-es
rm -R ../src/main/res/values-fa
rm -R ../src/main/res/values-fr
rm -R ../src/main/res/values-hu
rm -R ../src/main/res/values-ja
rm -R ../src/main/res/values-tr

# Copy downloaded files to res directory
cp -R values-ar-rSA ../src/main/res/values-ar
cp -R values-cs-rCZ ../src/main/res/values-cs
cp -R values-de-rDE ../src/main/res/values-de
cp -R values-en-rUS ../src/main/res/values-en
cp -R values-es-rES ../src/main/res/values-es
cp -R values-fa-rIR ../src/main/res/values-fa
cp -R values-fr-rFR ../src/main/res/values-fr
cp -R values-hu-rHU ../src/main/res/values-hu
cp -R values-ja-rJP ../src/main/res/values-ja
cp -R values-tr-rTR ../src/main/res/values-tr

# Delete temporary directory
cd ..
rm -R tmp

# Show changes
git status
