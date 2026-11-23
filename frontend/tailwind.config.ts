import type { Config } from "tailwindcss";

/**
 * Paleta do catálogo Studio Giovanna Pugliesi.
 * - bege.DEFAULT (#E1D4C8) e marrom.DEFAULT (#563F38) foram amostrados do PDF.
 * - As demais variações são derivadas para superfícies, bordas e estados.
 */
const config: Config = {
  content: ["./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        bege: {
          50: "#F5EFE9",
          100: "#EDE3DA",
          DEFAULT: "#E1D4C8",
          300: "#D3C2B3",
          400: "#BFA998",
        },
        marrom: {
          300: "#9A8077",
          400: "#7A5F56",
          DEFAULT: "#563F38",
          700: "#42302A",
          800: "#33241F",
        },
      },
      fontFamily: {
        serif: ["var(--font-playfair)", "Georgia", "serif"],
        sans: ["var(--font-poppins)", "system-ui", "sans-serif"],
      },
      borderRadius: {
        card: "1.25rem",
      },
    },
  },
  plugins: [],
};

export default config;
