// src/components/pages/ReviewerDashboard.jsx
import React, { useState, useEffect } from 'react';
import api from '../services/api';

const ReviewerDashboard = () => {
    const [readyAssignments, setReadyAssignments] = useState([]);
    const [resubmittedAssignments, setResubmittedAssignments] = useState([]);

    useEffect(() => {
        api.get('/assignments/ready')
            .then((response) => setReadyAssignments(response.data));
        api.get('/assignments/resubmitted')
            .then((response) => setResubmittedAssignments(response.data));
    }, []);

    const claimAssignment = (id) => {
        api.post(`/assignments/claim/${id}`).then(() => window.location.reload());
    };

    const reclaimAssignment = (id) => {
        api.post(`/assignments/reclaim/${id}`).then(() => window.location.reload());
    };

    return (
        <div>
            <h1>Reviewer Dashboard</h1>
            <h2>Ready for Review</h2>
            {readyAssignments.map((assign) => (
                <div key={assign.id}>
                    <h3>{assign.title}</h3>
                    <button onClick={() => claimAssignment(assign.id)}>Claim</button>
                </div>
            ))}
            <h2>Resubmitted Assignments</h2>
            {resubmittedAssignments.map((assign) => (
                <div key={assign.id}>
                    <h3>{assign.title}</h3>
                    <button onClick={() => reclaimAssignment(assign.id)}>Reclaim</button>
                </div>
            ))}
        </div>
    );
};

export default ReviewerDashboard;