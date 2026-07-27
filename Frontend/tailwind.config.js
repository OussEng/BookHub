/** @type {import('tailwindcss').Config} */

const defaultTheme = require('tailwindcss/defaultTheme');

module.exports = {
  content: [
    './src/**/*.{html,ts}',
  ],
  theme: {
    extend: {fontFamily: {
        sans: ['Garamond', ...defaultTheme.fontFamily.serif],
      },
    },
  },
  plugins: [],
};
