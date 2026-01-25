import api from "../api/axios.js";
import { useEffect, useState } from "react";


export const uploadFile = (formData) => {
    return api.post("/api/products/file/upload", formData, {
        headers: {
            "Content-Type": "multipart/form-data"
        }
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

    const loadFiles = async () => {
        const res = await getFiles();
        setFiles(res.data);
    };

    useEffect(() => {
        loadFiles();
    }, []);

    const handleUpload = async () => {
        const formData = new FormData();
        formData.append("file", file);
        formData.append("fileType", fileType);

        await uploadFile(formData);
        setFile(null);
        setFileType("");
        loadFiles();
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
        <div className="container mt-4">
            <h3>File Upload</h3>
            <table width="50%" border="1" className="table mt-4">
              <tr>
                <td>File Type</td>
                <td>
                        <select value={fileType} onChange={e => setFileType(e.target.value)}>
                            <option value="">Select Type</option>
                            <option value="Product">Product File</option>
                            <option value="User">User File</option>
                        </select>

                 </td>
              </tr>
              <tr>
                <td>Select File</td>
                <td>
                     <input type="file"  onChange={e => setFile(e.target.files[0])} />

                 </td>
              </tr>
              <tr>
                <td></td>
                <td>
                                        <button onClick={handleUpload}>Upload</button>
                                    </td>
               </tr>
            </table>


            <table className="table mt-4">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>File</th>
                        <th>Type</th>
                        <th>Status</th>
                        <th>Uploaded On</th>
                        <th>Uploaded By</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {files.map(f => (
                        <tr key={f.id}>
                            <td>{f.id}</td>
                            <td>{f.fileName}</td>
                            <td>{f.fileType}</td>
                            <td>{f.status}</td>
                            <td>{f.uploadedOn}</td>
                            <td>{f.uploadedBy}</td>
                            <td>
                                <button onClick={() => handleRaw(f.id)} className="btn btn-sm btn-info">Raw</button>
                                <button onClick={() => handleDelete(f.id)} className="btn btn-sm btn-danger ms-2">Delete</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

        </div>
    );
}
