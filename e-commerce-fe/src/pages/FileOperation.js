import api from "../api/axios";
import { useEffect, useState } from "react";
import "./css/PagesCommon.css";
import "./css/FileOperation.css";

export const uploadFile = (formData) => {
  return api.post("/api/products/file/upload", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

export const getFiles = () => {
  return api.get("/api/products/file/list");
};

export const deleteFile = (id) => {
  return api.delete(`/files/${id}`);
};

export const getRaw = (id) => {
  return api.get(`/files/raw/${id}`);
};

export default function FileOperation() {
  const [files, setFiles] = useState([]);
  const [file, setFile] = useState(null);
  const [fileType, setFileType] = useState("");
  const [loading, setLoading] = useState(false);

  const loadFiles = async () => {
    const res = await getFiles();
    setFiles(res.data || []);
  };

  useEffect(() => {
    loadFiles();
  }, []);

  const handleUpload = async () => {
    setLoading(true);
    try {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("fileType", fileType);
      await uploadFile(formData);
      setFile(null);
      setFileType("");
      loadFiles();
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    await deleteFile(id);
    loadFiles();
  };

  const handleRaw = async (id) => {
    const res = await getRaw(id);
    console.log(res.data);
    alert(JSON.stringify(res.data, null, 2));
  };

  return (
    <div className="fileop-wrap">
      <h1 className="page-title">File upload</h1>

      <div className="page-card fileop-upload-card">
        <h2>Upload file</h2>
        <div className="fileop-upload-grid">
          <label htmlFor="fileop-type">File type</label>
          <select
            id="fileop-type"
            className="page-input"
            value={fileType}
            onChange={(e) => setFileType(e.target.value)}
          >
            <option value="">Select type</option>
            <option value="Product">Product file</option>
            <option value="User">User file</option>
          </select>

          <label htmlFor="fileop-file">Select file</label>
          <div className="row-actions">
            <input
              id="fileop-file"
              type="file"
              className="page-input"
              onChange={(e) => setFile(e.target.files?.[0] ?? null)}
            />
          </div>

          <span />
          <div className="row-actions">
            <button
              type="button"
              className="btn btn-primary"
              onClick={handleUpload}
              disabled={loading || !file || !fileType}
            >
              {loading ? "Uploading…" : "Upload"}
            </button>
            {loading && <div className="spinner" />}
          </div>
        </div>
      </div>

      <div className="page-card fileop-table-card">
        <h2>Uploaded files</h2>
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>File</th>
              <th>Type</th>
              <th>Status</th>
              <th>Details</th>
              <th>Uploaded on</th>
              <th>Uploaded by</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {files.map((f) => (
              <tr key={f.id}>
                <td>{f.id}</td>
                <td>{f.fileName}</td>
                <td>{f.fileType}</td>
                <td>{f.status}</td>
                <td>{f.detail}</td>
                <td>{f.uploadedOn}</td>
                <td>{f.uploadedBy}</td>
                <td>
                  <div className="cell-actions">
                    <button
                      type="button"
                      className="btn btn-secondary btn-sm"
                      onClick={() => handleRaw(f.id)}
                    >
                      Raw
                    </button>
                    <button
                      type="button"
                      className="btn btn-danger btn-sm"
                      onClick={() => handleDelete(f.id)}
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
