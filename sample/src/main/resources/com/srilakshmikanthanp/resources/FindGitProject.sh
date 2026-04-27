echo "== Git Project Check =="

DIR="$PWD"

while [ "$DIR" != "/" ]; do
  if [ -d "$DIR/.git" ]; then
    echo "Git repository found at: $DIR"
    cd "$DIR"
    git status
    exit 0
  fi
  DIR="$(dirname "$DIR")"
done

echo "No git repository found in current or parent directories"

echo "== Done =="
