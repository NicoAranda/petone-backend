import express from 'express'

const router = express.Router()

const PUBLICATION_SERVICE = process.env.PUBLICATION_SERVICE || 'http://localhost:8080'
const USER_SERVICE = process.env.USER_SERVICE || 'http://localhost:8081'
const STORIES_SERVICE = process.env.STORIES_SERVICE || 'http://localhost:8082'

const forwardHeaders = (req) => {
  const headers = {}
  if (req.headers.authorization) headers['Authorization'] = req.headers.authorization
  return headers
}

const fetchWithTimeout = async (url, opts = {}, timeout = 8000) => {
  const controller = new AbortController()
  const id = setTimeout(() => controller.abort(), timeout)
  try {
    const res = await fetch(url, { signal: controller.signal, ...opts })
    clearTimeout(id)
    return res
  } catch (err) {
    clearTimeout(id)
    throw err
  }
}

router.get('/publicaciones', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(
      `${PUBLICATION_SERVICE}/api/publicaciones`,
      { headers: forwardHeaders(req) }
    )
    const publicaciones = await resp.json()
    if (!resp.ok) {
      return res.status(resp.status).json(publicaciones)
    }
    const publicacionesConUsuario = await Promise.all(
      publicaciones.map(async (pub) => {
        try {

          const userResp = await fetchWithTimeout(
            `${USER_SERVICE}/api/usuarios/${pub.userId}`,
            { headers: forwardHeaders(req) }
          )
          if (!userResp.ok) {

            console.log(
              `Error usuario ${pub.userId}:`,
              userResp.status
            )

            const text = await userResp.text()

            console.log(text)

            return {
              ...pub,
              usuario: null
            }
          }
          const usuario = await userResp.json()
          return {
            ...pub,
            usuario: {
              id: usuario.id,
              nombre: usuario.nombre,
              apellido: usuario.apellido,
              email: usuario.email
            }
          }
        } catch (error) {
          console.error(
            `Error obteniendo usuario ${pub.userId}:`,
            error.message
          )
          return {
            ...pub,
            usuario: null
          }
        }
      })
    )

    return res.json(publicacionesConUsuario)

  } catch (err) {
    console.error('bff /publicaciones error', err?.message || err)

    return res.status(502).json({
      error: 'Error fetching publicaciones'
    })
  }
})

router.get('/publicaciones/reportadas', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/publicaciones/reportadas`, { headers: forwardHeaders(req) })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff /publicaciones/reportadas error', err?.message || err)
    return res.status(502).json({ error: 'Error fetching reported publicaciones' })
  }
})

router.get('/publicaciones/usuario/:userId', async (req, res) => {
  try {

    const resp = await fetchWithTimeout(
      `${PUBLICATION_SERVICE}/api/publicaciones/usuario/${req.params.userId}`,
      {
        headers: forwardHeaders(req)
      }
    );

    const data = await resp.json();

    if (!resp.ok) {
      return res.status(resp.status).json(data);
    }

    return res.json(data);

  } catch (err) {

    console.error(
      'bff GET /publicaciones/usuario/:userId error',
      err?.message || err
    );

    return res.status(502).json({
      error: 'Error fetching publicaciones del usuario'
    });

  }
});

router.get('/publicaciones/:id', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/publicaciones/${req.params.id}`, { headers: forwardHeaders(req) })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff /publicaciones/:id error', err?.message || err)
    return res.status(502).json({ error: 'Error fetching publicación' })
  }
})

router.patch('/publicaciones/:id/reportar', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/publicaciones/${req.params.id}/reportar`, { method: 'PATCH', headers: forwardHeaders(req) })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff PATCH /publicaciones/:id/reportar error', err?.message || err)
    return res.status(502).json({ error: 'Error reporting publicación' })
  }
})

router.put('/publicaciones/:id', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/publicaciones/${req.params.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', ...forwardHeaders(req) },
      body: JSON.stringify(req.body)
    })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff PUT /publicaciones/:id error', err?.message || err)
    return res.status(502).json({ error: 'Error updating publicación' })
  }
})

router.delete('/publicaciones/:id', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/publicaciones/${req.params.id}`, {
      method: 'DELETE',
      headers: forwardHeaders(req)
    })
    const data = await resp.json().catch(() => null)
    if (!resp.ok) return res.status(resp.status).json(data || { error: 'Upstream error' })
    return res.json(data)
  } catch (err) {
    console.error('bff DELETE /publicaciones/:id error', err?.message || err)
    return res.status(502).json({ error: 'Error deleting publicación' })
  }
})

router.post('/publicaciones/con-imagenes', async (req, res) => {
  try {
    const headers = { ...(req.headers || {}) }
    delete headers.host

    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/publicaciones/con-imagenes`, {
      method: 'POST',
      body: req,
      headers,
      duplex: 'half'
    }, 20000)

    const pubData = await resp.json().catch(() => null)
    if (!resp.ok) return res.status(resp.status).json(pubData || { error: 'Upstream error' })

    if (pubData && pubData.id) {
      try {
        const primeraFoto = pubData.fotos && pubData.fotos.length > 0 
          ? pubData.fotos[0].url 
          : 'https://picsum.photos/400/700?random=default';

        const storyPayload = {
          user: pubData.nombre || 'Mascota PetOne',
          avatar: 'https://i.pravatar.cc/100?img=1',
          img: primeraFoto,
          publicationId: pubData.id
        };

        await fetchWithTimeout(`${STORIES_SERVICE}/api/stories`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            ...forwardHeaders(req)
          },
          body: JSON.stringify(storyPayload)
        }, 3000);
        
        console.log(`[BFF] Historia autogenerada con éxito para la publicación #${pubData.id}`);
      } catch (storyErr) {
        console.error('[BFF Mantenible] Error crítico al generar historia automática (Fallback activo):', storyErr.message);
      }
    }

    return res.json(pubData)
  } catch (err) {
    console.error('bff POST /publicaciones/con-imagenes error', err?.message || err)
    return res.status(502).json({ error: 'Error uploading publicación' })
  }
})

router.get('/stories', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${STORIES_SERVICE}/api/stories`, { headers: forwardHeaders(req) })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff GET /stories error', err?.message || err)
    return res.status(502).json({ error: 'Error fetching historias' })
  }
})

router.delete('/stories/:id', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${STORIES_SERVICE}/api/stories/${req.params.id}`, {
      method: 'DELETE',
      headers: forwardHeaders(req)
    })
    const data = await resp.json().catch(() => null)
    if (!resp.ok) return res.status(resp.status).json(data || { error: 'Upstream error' })
    return res.json(data)
  } catch (err) {
    console.error('bff DELETE /stories/:id error', err?.message || err)
    return res.status(502).json({ error: 'Error deleting historia' })
  }
})

router.get('/reportes', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/reportes`, { headers: forwardHeaders(req) })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff GET /reportes error', err?.message || err)
    return res.status(502).json({ error: 'Error fetching reportes' })
  }
})

router.get('/reportes/:id', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/reportes/${req.params.id}`, { headers: forwardHeaders(req) })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff GET /reportes/:id error', err?.message || err)
    return res.status(502).json({ error: 'Error fetching reporte' })
  }
})

router.post('/reportes', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/reportes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...forwardHeaders(req) },
      body: JSON.stringify(req.body)
    })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff POST /reportes error', err?.message || err)
    return res.status(502).json({ error: 'Error creating reporte' })
  }
})

router.put('/reportes/:id/estado', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/reportes/${req.params.id}/estado?nuevoEstado=${encodeURIComponent(req.query.nuevoEstado)}`, {
      method: 'PUT',
      headers: forwardHeaders(req)
    })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff PUT /reportes/:id/estado error', err?.message || err)
    return res.status(502).json({ error: 'Error updating reporte estado' })
  }
})

router.put('/reportes/:id/responder', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/reportes/${req.params.id}/responder`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', ...forwardHeaders(req) },
      body: JSON.stringify(req.body)
    })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff PUT /reportes/:id/responder error', err?.message || err)
    return res.status(502).json({ error: 'Error responding reporte' })
  }
})

router.delete('/reportes/:id', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/reportes/${req.params.id}`, {
      method: 'DELETE',
      headers: forwardHeaders(req)
    })
    const data = await resp.json().catch(() => null)
    if (!resp.ok) return res.status(resp.status).json(data || { error: 'Upstream error' })
    return res.json(data)
  } catch (err) {
    console.error('bff DELETE /reportes/:id error', err?.message || err)
    return res.status(502).json({ error: 'Error deleting reporte' })
  }
})


router.post('/usuarios/login', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${USER_SERVICE}/api/usuarios/login`, { method: 'POST', headers: { 'Content-Type': 'application/json', ...forwardHeaders(req) }, body: JSON.stringify(req.body) })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff POST /usuarios/login error', err?.message || err)
    return res.status(502).json({ error: 'Error logging in' })
  }
})

router.post('/usuarios/registro-cliente', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${USER_SERVICE}/api/usuarios/registro-cliente`, { method: 'POST', headers: { 'Content-Type': 'application/json', ...forwardHeaders(req) }, body: JSON.stringify(req.body) })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff POST /usuarios/registro-cliente error', err?.message || err)
    return res.status(502).json({ error: 'Error registering usuario' })
  }
})

router.get('/usuarios', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${USER_SERVICE}/api/usuarios`, { headers: forwardHeaders(req) })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff GET /usuarios error', err?.message || err)
    return res.status(502).json({ error: 'Error fetching usuarios' })
  }
})

router.get('/usuarios/:id', async (req, res) => {
  try {
    const resp = await fetchWithTimeout(`${USER_SERVICE}/api/usuarios/${req.params.id}`, { headers: forwardHeaders(req) })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff GET /usuarios/:id error', err?.message || err)
    return res.status(502).json({ error: 'Error fetching usuario' })
  }
})

export default router