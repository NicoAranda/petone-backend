import express from 'express'

const router = express.Router()

const PUBLICATION_SERVICE = process.env.PUBLICATION_SERVICE || 'http://localhost:8080'
const USER_SERVICE = process.env.USER_SERVICE || 'http://localhost:8081'

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
    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/publicaciones`, { headers: forwardHeaders(req) })
    const data = await resp.json()
    if (!resp.ok) return res.status(resp.status).json(data)
    return res.json(data)
  } catch (err) {
    console.error('bff /publicaciones error', err?.message || err)
    return res.status(502).json({ error: 'Error fetching publicaciones' })
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

// File upload proxy: forward multipart stream directly to publication service without parsing
router.post('/publicaciones/con-imagenes', async (req, res) => {
  try {
    // Forward original headers but remove host to avoid conflicts
    const headers = { ...(req.headers || {}) }
    delete headers.host

    const resp = await fetchWithTimeout(`${PUBLICATION_SERVICE}/api/publicaciones/con-imagenes`, {
      method: 'POST',
      body: req,
      headers,
      duplex: 'half'
    }, 20000)

    // stream response back
    const data = await resp.json().catch(() => null)
    if (!resp.ok) return res.status(resp.status).json(data || { error: 'Upstream error' })
    return res.json(data)
  } catch (err) {
    console.error('bff POST /publicaciones/con-imagenes error', err?.message || err)
    return res.status(502).json({ error: 'Error uploading publicación' })
  }
})

// User routes proxy (login, register, list, get by id)
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
