
const estado = {
  tema: "claro",
  tamanoFuente: 14,
  leccionActivaId: null,
};

const FUENTE_MIN = 11;
const FUENTE_MAX = 22;


const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => Array.from(document.querySelectorAll(selector));


function inicializarMenus() {
  const menus = $$(".menu");

  menus.forEach((menu) => {
    const trigger = menu.querySelector(".menu__trigger");
    trigger.addEventListener("click", (evento) => {
      evento.stopPropagation();
      const yaAbierto = menu.classList.contains("is-open");
      cerrarTodosLosMenus();
      if (!yaAbierto) menu.classList.add("is-open");
    });
  });

  document.addEventListener("click", cerrarTodosLosMenus);

  $$(".menu__panel").forEach((panel) => {
    panel.addEventListener("click", (evento) => evento.stopPropagation());
  });
}

function cerrarTodosLosMenus() {
  $$(".menu.is-open").forEach((menu) => menu.classList.remove("is-open"));
}


function inicializarConfiguracion() {
  $("#fontMas").addEventListener("click", () => cambiarTamanoFuente(1));
  $("#fontMenos").addEventListener("click", () => cambiarTamanoFuente(-1));

  $$(".menu__pill[data-tema]").forEach((boton) => {
    boton.addEventListener("click", () => aplicarTema(boton.dataset.tema));
  });

  actualizarValorFuenteEnPantalla();
}

function cambiarTamanoFuente(delta) {
  const nuevo = estado.tamanoFuente + delta;
  if (nuevo < FUENTE_MIN || nuevo > FUENTE_MAX) return;

  estado.tamanoFuente = nuevo;
  document.documentElement.style.setProperty("--font-editor-size", `${nuevo}px`);
  actualizarValorFuenteEnPantalla();
}

function actualizarValorFuenteEnPantalla() {
  $("#fontValor").textContent = `${estado.tamanoFuente}px`;
}

function aplicarTema(tema) {
  estado.tema = tema;
  document.documentElement.setAttribute("data-theme", tema);

  $$(".menu__pill[data-tema]").forEach((boton) => {
    boton.classList.toggle("is-active", boton.dataset.tema === tema);
  });
}


async function cargarListaDeLecciones() {
  const contenedor = $("#listaLecciones");

  try {
    const respuesta = await fetch("lecciones/manifest.json");
    if (!respuesta.ok) throw new Error("No se pudo leer manifest.json");
    const lecciones = await respuesta.json();

    if (lecciones.length === 0) {
      contenedor.innerHTML = '<li class="menu__empty">No hay lecciones disponibles</li>';
      return;
    }

    contenedor.innerHTML = "";
    lecciones.forEach((leccion) => {
      const item = document.createElement("li");
      const boton = document.createElement("button");
      boton.className = "menu__item";
      boton.textContent = leccion.titulo;
      boton.addEventListener("click", () => abrirLeccion(leccion.id, leccion.titulo));
      item.appendChild(boton);
      contenedor.appendChild(item);
    });
  } catch (error) {
    contenedor.innerHTML = '<li class="menu__empty">Error al cargar lecciones</li>';
    console.error(error);
  }
}

async function abrirLeccion(id, titulo) {
  cerrarTodosLosMenus();

  const contenedor = $("#leccionContenido");
  contenedor.innerHTML = '<p class="leccion-vacia">Cargando lección…</p>';

  try {
    const baseUrl = `lecciones/${id}/`;

    const [respuestaMd, respuestaConfig] = await Promise.all([
      fetch(`${baseUrl}leccion.md`),
      fetch(`${baseUrl}config.txt`),
    ]);

    if (!respuestaMd.ok) throw new Error(`No se encontró leccion.md para ${id}`);

    const textoMarkdown = await respuestaMd.text();
    const textoConfig = respuestaConfig.ok ? await respuestaConfig.text() : "";
    const objetivo = extraerObjetivo(textoConfig);

    contenedor.innerHTML = markdownAHtml(textoMarkdown, baseUrl);

    estado.leccionActivaId = id;
    $("#tituloLeccion").textContent = titulo;

    resetearConsola();
    $("#editorCodigo").value = "";

    console.info(`Lección "${id}" abierta. Objetivo: ${objetivo || "no definido"}`);
  } catch (error) {
    contenedor.innerHTML = `<p class="leccion-vacia">No se pudo abrir la lección: ${error.message}</p>`;
    console.error(error);
  }
}

function cerrarLeccion() {
  cerrarTodosLosMenus();

  estado.leccionActivaId = null;
  $("#tituloLeccion").textContent = "Ninguna lección abierta";
  $("#leccionContenido").innerHTML =
    '<p class="leccion-vacia">Abre una lección desde el menú <strong>Lecciones</strong> para ver aquí sus instrucciones.</p>';
  $("#editorCodigo").value = "";
  resetearConsola();
}

function extraerObjetivo(textoConfig) {
  const linea = textoConfig.split("\n").find((l) => l.trim().startsWith("objetivo="));
  return linea ? linea.trim().split("=")[1] : null;
}

function resetearConsola() {
  const consola = $("#consolaSalida");
  consola.textContent = "Esperando compilación o ejecución…";
  consola.classList.remove("es-error", "es-exito");
}


function markdownAHtml(md, baseUrl) {
  const lineas = md.replace(/\r\n/g, "\n").split("\n");
  const html = [];

  let dentroDeBloqueDeCodigo = false;
  let bufferDeCodigo = [];
  let listaAbierta = null; // "ol" | "ul" | null

  const cerrarLista = () => {
    if (listaAbierta) {
      html.push(`</${listaAbierta}>`);
      listaAbierta = null;
    }
  };

  for (const lineaOriginal of lineas) {
    const linea = lineaOriginal;

    if (linea.trim().startsWith("```")) {
      if (!dentroDeBloqueDeCodigo) {
        cerrarLista();
        dentroDeBloqueDeCodigo = true;
        bufferDeCodigo = [];
      } else {
        html.push(`<pre><code>${escaparHtml(bufferDeCodigo.join("\n"))}</code></pre>`);
        dentroDeBloqueDeCodigo = false;
      }
      continue;
    }

    if (dentroDeBloqueDeCodigo) {
      bufferDeCodigo.push(linea);
      continue;
    }

    if (linea.startsWith("# ")) {
      cerrarLista();
      html.push(`<h1>${lineaEnLinea(linea.slice(2))}</h1>`);
      continue;
    }
    if (linea.startsWith("## ")) {
      cerrarLista();
      html.push(`<h2>${lineaEnLinea(linea.slice(3))}</h2>`);
      continue;
    }

    const itemOrdenado = linea.match(/^\d+\.\s+(.*)/);
    if (itemOrdenado) {
      if (listaAbierta !== "ol") {
        cerrarLista();
        html.push("<ol>");
        listaAbierta = "ol";
      }
      html.push(`<li>${lineaEnLinea(itemOrdenado[1])}</li>`);
      continue;
    }

    const itemNoOrdenado = linea.match(/^[-*]\s+(.*)/);
    if (itemNoOrdenado) {
      if (listaAbierta !== "ul") {
        cerrarLista();
        html.push("<ul>");
        listaAbierta = "ul";
      }
      html.push(`<li>${lineaEnLinea(itemNoOrdenado[1])}</li>`);
      continue;
    }

    if (linea.startsWith(">")) {
      cerrarLista();
      html.push(`<blockquote>${lineaEnLinea(linea.replace(/^>\s?/, ""))}</blockquote>`);
      continue;
    }

    cerrarLista();

    if (linea.trim() === "") continue;

    html.push(`<p>${lineaEnLinea(linea)}</p>`);
  }

  cerrarLista();
  return html.join("\n");

  function lineaEnLinea(texto) {
    let resultado = escaparHtml(texto);

    resultado = resultado.replace(
      /!\[([^\]]*)\]\(([^)]+)\)/g,
      (_, alt, src) => `<img src="${baseUrl}${src}" alt="${alt}">`
    );

    resultado = resultado.replace(/`([^`]+)`/g, "<code>$1</code>");
    resultado = resultado.replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>");
    resultado = resultado.replace(/\*([^*]+)\*/g, "<em>$1</em>");

    return resultado;
  }
}

function escaparHtml(texto) {
  return texto
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}


function inicializarAyuda() {
  const modal = $("#modalAyuda");

  $("#btnAbrirAyuda").addEventListener("click", () => {
    cerrarTodosLosMenus();
    modal.hidden = false;
  });

  $("#btnCerrarAyuda").addEventListener("click", () => (modal.hidden = true));

  modal.addEventListener("click", (evento) => {
    if (evento.target === modal) modal.hidden = true;
  });
}

function inicializar() {
  inicializarMenus();
  inicializarConfiguracion();
  inicializarAyuda();

  $("#btnCerrarLeccion").addEventListener("click", cerrarLeccion);

  cargarListaDeLecciones();
}

document.addEventListener("DOMContentLoaded", inicializar);
