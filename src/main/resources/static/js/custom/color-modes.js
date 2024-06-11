function getStoredTheme() {
  return localStorage.getItem('theme');
}

function setStoredTheme(theme) {
  localStorage.setItem('theme', theme);
}

function getPreferredTheme() {
  const storedTheme = getStoredTheme();
  return storedTheme || (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
}

function setTheme(theme) {
  const preferredTheme = (theme === 'auto') ? (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light') : theme;
  document.documentElement.setAttribute('data-bs-theme', preferredTheme);
}

function toggleElements(theme) {
  const images = document.querySelectorAll('img:not(.nocambiar)');
  images.forEach(img => {
    const src = img.src;
    if (theme === 'dark' && src.includes('Positivo.svg')) {
      img.src = src.replace('Positivo.svg', 'Negativo.svg');
    } else if (theme === 'light' && src.includes('Negativo.svg')) {
      img.src = src.replace('Negativo.svg', 'Positivo.svg');
    }
  });
}

function showActiveTheme(theme) {
  const themeSwitcher = document.querySelector('#bd-theme');
  if (!themeSwitcher) return;

  const themeSwitcherText = document.querySelector('#bd-theme-text');
  const activeThemeIcon = document.querySelector('.theme-icon-active use');
  const btnToActive = document.querySelector(`[data-bs-theme-value="${theme}"]`);
  const svgOfActiveBtn = btnToActive.querySelector('svg use').getAttribute('href');

  document.querySelectorAll('[data-bs-theme-value]').forEach(element => {
    element.classList.remove('active');
    element.setAttribute('aria-pressed', 'false');
  });

  btnToActive.classList.add('active');
  btnToActive.setAttribute('aria-pressed', 'true');
  activeThemeIcon.setAttribute('href', svgOfActiveBtn);

  const themeSwitcherLabel = `${themeSwitcherText.textContent} (${btnToActive.dataset.bsThemeValue})`;
  themeSwitcher.setAttribute('aria-label', themeSwitcherLabel);
  themeSwitcher.focus();
}

function handleThemeSwitchChange() {
  const theme = (document.documentElement.getAttribute('data-bs-theme') === 'light') ? 'dark' : 'light';
  setStoredTheme(theme);
  setTheme(theme);
  showActiveTheme(theme);
  toggleElements(theme);
}

function observeDOM(obj, callback) {
  const MutationObserver = window.MutationObserver || window.WebKitMutationObserver;
  const eventListenerSupported = window.addEventListener;

  if (MutationObserver) {
    const obs = new MutationObserver(function (mutations, observer) {
      if (mutations[0].addedNodes.length || mutations[0].removedNodes.length) {
        callback();
      }
    });
    obs.observe(obj, { childList: true, subtree: true });
  } else if (eventListenerSupported) {
    obj.addEventListener('DOMNodeInserted', callback, false);
    obj.addEventListener('DOMNodeRemoved', callback, false);
  }
}

(() => {
  'use strict';

  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
    const storedTheme = getStoredTheme();
    if (!storedTheme || (storedTheme !== 'light' && storedTheme !== 'dark')) {
      setTheme(getPreferredTheme());
    }
  });

  window.addEventListener('DOMContentLoaded', () => {
    setTheme(getPreferredTheme());
    showActiveTheme(getPreferredTheme());

    document.querySelectorAll('[data-bs-theme-value]').forEach(toggle => {
      toggle.addEventListener('click', () => {
        const theme = toggle.getAttribute('data-bs-theme-value');
        setStoredTheme(theme);
        setTheme(theme);
        showActiveTheme(theme);
        toggleElements(theme);
      });
    });

    observeDOM(document.body, function () {
      const themeSwitch = document.querySelector('#theme-switch');
      if (themeSwitch) {
        themeSwitch.addEventListener('change', handleThemeSwitchChange);
      }
    });
  });

})();