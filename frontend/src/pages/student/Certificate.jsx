import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getCertificate } from "../../services/courseService.js";

export default function Certificate() {
  const { courseId } = useParams();
  const [certificate, setCertificate] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    getCertificate(courseId)
      .then((data) => { if (active) setCertificate(data); })
      .catch(() => { if (active) setError("This certificate isn’t available yet."); });
    return () => { active = false; };
  }, [courseId]);

  if (error) return <div className="empty-state"><p>{error}</p><Link to={`/courses/${courseId}`}>Return to the course</Link></div>;
  if (!certificate) return <p className="muted">Preparing your certificate…</p>;

  return (
    <div className="certificate-page">
      <div className="certificate-actions"><Link to={`/courses/${courseId}`}>← Back to course</Link><button className="button-primary" onClick={() => window.print()}>Download / print</button></div>
      <article className="certificate-sheet">
        <span className="eyebrow">CERTIFICATE OF COMPLETION</span>
        <div className="certificate-rule" />
        <p className="certificate-intro">This certificate is proudly presented to</p>
        <h1>{certificate.learnerName}</h1>
        <p className="certificate-copy">for successfully completing</p>
        <h2>{certificate.courseTitle}</h2>
        <div className="certificate-footer">
          <div><span>ISSUED</span><strong>{new Date(certificate.issuedAt).toLocaleDateString()}</strong></div>
          <div><span>CERTIFICATE ID</span><strong>{certificate.certificateCode}</strong></div>
          <div className="certificate-seal">N</div>
        </div>
      </article>
    </div>
  );
}
