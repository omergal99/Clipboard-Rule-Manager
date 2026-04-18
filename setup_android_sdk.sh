#!/usr/bin/env bash
set -euo pipefail

SDK_ROOT="$HOME/Android/Sdk"
TOOLS_ZIP="$HOME/commandlinetools-linux.zip"
TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"

echo "📦 Installing Android SDK to: $SDK_ROOT"
echo ""

# Create SDK directory structure
mkdir -p "$SDK_ROOT/cmdline-tools"
cd "$HOME"

# Download command-line tools if not already present
if [ ! -f "$TOOLS_ZIP" ]; then
  echo "⬇️  Downloading Android command-line tools..."
  wget -O "$TOOLS_ZIP" "$TOOLS_URL"
else
  echo "✅ Command-line tools already downloaded"
fi

# Extract and organize
echo "📂 Extracting and organizing command-line tools..."
rm -rf "$SDK_ROOT/cmdline-tools/latest" "$SDK_ROOT/cmdline-tools/tmp"
unzip -q "$TOOLS_ZIP" -d "$SDK_ROOT/cmdline-tools/tmp"
mv "$SDK_ROOT/cmdline-tools/tmp/cmdline-tools" "$SDK_ROOT/cmdline-tools/latest"
rm -rf "$SDK_ROOT/cmdline-tools/tmp"

# Set environment variables for this script
export ANDROID_HOME="$SDK_ROOT"
export ANDROID_SDK_ROOT="$SDK_ROOT"
export PATH="$PATH:$SDK_ROOT/cmdline-tools/latest/bin:$SDK_ROOT/platform-tools"

# Install required SDK packages
echo ""
echo "📥 Installing SDK platforms and build-tools..."
sdkmanager --sdk_root="$SDK_ROOT" "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Accept licenses
echo ""
echo "✅ Accepting Android SDK licenses..."
yes | sdkmanager --licenses

# Create environment file for future sessions
echo ""
echo "💾 Creating environment file: ~/.android-sdk-env.sh"
cat > "$HOME/.android-sdk-env.sh" <<EOF
#!/usr/bin/env bash
# Android SDK Environment Setup
export ANDROID_HOME=$SDK_ROOT
export ANDROID_SDK_ROOT=\$ANDROID_HOME
export PATH=\$PATH:$SDK_ROOT/cmdline-tools/latest/bin:$SDK_ROOT/platform-tools
EOF

chmod +x "$HOME/.android-sdk-env.sh"

# Display next steps
echo ""
echo "✅ ============================================"
echo "✅ Android SDK installation complete!"
echo "✅ ============================================"
echo ""
echo "📋 Next steps:"
echo ""
echo "1. Load the environment variables:"
echo "   source ~/.android-sdk-env.sh"
echo ""
echo "2. Create local.properties in your project:"
echo "   echo 'sdk.dir=$SDK_ROOT' > /home/omer/DEV/ai-start/clipboard-rules/local.properties"
echo ""
echo "3. Run tests:"
echo "   cd /home/omer/DEV/ai-start/clipboard-rules"
echo "   ./gradlew test"
echo ""
echo "4. Build APK:"
echo "   ./gradlew assembleDebug"
echo ""
