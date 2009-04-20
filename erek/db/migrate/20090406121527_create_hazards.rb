class CreateHazards < ActiveRecord::Migration
  def self.up
    create_table :hazards do |t|
      t.string :code
      t.string :name

      t.timestamps
    end
  end

  def self.down
    drop_table :hazards
  end
end
