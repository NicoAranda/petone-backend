import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const filePath = path.join(__dirname, 'tempimage.jpg');
fs.writeFileSync(filePath, 'test');

const form = new FormData();
form.append('nombre', 'DebugTestPet');
form.append('raza', 'Perro');
form.append('tamano', 'Medio');
form.append('estado', 'Activo');
form.append('descripcion', 'debug');
form.append('usuarioId', '1');
const blob = new Blob([fs.readFileSync(filePath)], { type: 'image/jpeg' });
form.append('fotos', blob, 'tempimage.jpg');

(async () => {
  try {
    const res = await fetch('http://localhost:8082/bff/mascotas', {
      method: 'POST',
      headers: {
        Authorization: 'Bearer dummy.token',
        Origin: 'http://localhost:5173',
        Accept: 'application/json'
      },
      body: form
    });
    console.log('status', res.status);
    console.log('headers', Object.fromEntries(res.headers.entries()));
    const text = await res.text();
    console.log('body', text);
  } catch (err) {
    console.error('error', err);
  } finally {
    fs.unlinkSync(filePath);
  }
})();
