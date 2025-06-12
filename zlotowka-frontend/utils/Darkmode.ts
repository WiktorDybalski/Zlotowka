export function applyDarkMode(darkMode: boolean) {
  const html = document.documentElement;
  if (darkMode) html.classList.add("dark");
  else html.classList.remove("dark");
}
