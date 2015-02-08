# !/bin/bash
# By Nico Alt
# See "LICENSE" for license information

# Download and unzip files
mkdir tmp
cd tmp
wget https://crowdin.com/download/project/hw-manager.zip
unzip hw-manager.zip

# Delete files in app directory
rm -R ../app/src/main/res/values-{ar,cs,de,en,es,fa,fr,hu}

# Copy downloaded files to res directory
cp -R values-ar-rSA ../app/src/main/res/values-ar
cp -R values-cs-rCZ ../app/src/main/res/values-cs
cp -R values-de-rDE ../app/src/main/res/values-de
cp -R values-en-rUS ../app/src/main/res/values-en
cp -R values-es-rES ../app/src/main/res/values-es
cp -R values-fa-rIR ../app/src/main/res/values-fa
cp -R values-fr-rFR ../app/src/main/res/values-fr
cp -R values-hu-rHU ../app/src/main/res/values-hu

# Delete temporary directory
cd ..
rm -R tmp

# Show changes
git status

echo
echo "Don't forget to reformat the code with Android Studio!"
echo
