using UnityEngine;

[RequireComponent(typeof(MazeDataParser))]
[RequireComponent(typeof(MazeMeshGenerator))]
[RequireComponent(typeof(StimuliGenerator))]
public class MazeConstructor : MonoBehaviour
{

    #region public members
    public bool showDebug;
    public Vector2Int StartCell { get => dataParser.startCell; }
    #endregion

    #region private members
    private MazeDataParser dataParser;
    private MazeMeshGenerator meshGenerator;
    private StimuliGenerator stimuliGenerator;
    #endregion

    #region methods
    void Awake()
    {
        dataParser = GetComponent<MazeDataParser>();
        meshGenerator = GetComponent<MazeMeshGenerator>();
        stimuliGenerator = GetComponent<StimuliGenerator>();
    }

    public void GenerateNewMaze(TextAsset config)
    {
        DisposeOldMaze();

        dataParser.LoadFromFile(config);

        DisplayMaze();
    }

    private void DisplayMaze()
    {
        meshGenerator.FromData(dataParser.mazeCells);

        //stimuliArea.createStimuliArea(new Vector3(0, 0, 0));

        foreach (var stimulus in dataParser.mazeStimuli)
        {
            stimuliGenerator.AddStimuli(stimulus);
        }
    }

    public void DisposeOldMaze()
    {
        var objects = GameObject.FindGameObjectsWithTag("Generated");
        foreach (var go in objects)
        {
            Destroy(go);
        }
    }
    #endregion

}
