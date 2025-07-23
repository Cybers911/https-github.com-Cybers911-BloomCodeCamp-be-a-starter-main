// src/components/pages/LearnerDashboard.jsx
import React, { useState, useEffect } from 'react';
import api from '../services/api';

const LearnerDashboard = () => {
    const [assignments, setAssignments] = useState([]);

    useEffect(() => {
        api.get('/assignments').then((response) => setAssignments(response.data));
    }, []);

    return (
        <div>
            <h1>Learner Dashboard</h1>
            <button onClick={() => window.location.href = '/create-assignment'}>
                Create Assignment
            </button>
            {assignments.map((assign) => (
                <div key={assign.id}>
                    <h3>{assign.title}</h3>
                    <p>Status: {assign.status}</p>
                    <button onClick={() => window.location.href = `/edit-assignment/${assign.id}`}>
                        Edit
                    </button>
                    <button onClick={() => window.location.href = `/view-assignment/${assign.id}`}>
                        View
                    </button>
                </div>
            ))}
        </div>
    );
};

export default LearnerDashboard;